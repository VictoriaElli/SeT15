package service;

import domain.model.*;
import dto.DepartureDTO;
import port.outbound.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;


public abstract class BaseScheduleService {

    protected final RouteRepositoryPort routeRepo;
    protected final RouteStopsRepositoryPort routeStopsRepo;
    protected final FrequencyRepositoryPort frequencyRepo;
    protected final ExceptionEntryRepositoryPort exceptionRepo;

    protected List<Route> allRoutes = new ArrayList<>();
    protected Map<Integer, List<Frequency>> scheduleMap = new HashMap<>();

    protected BaseScheduleService(RouteRepositoryPort routeRepo,
                                  RouteStopsRepositoryPort routeStopsRepo,
                                  FrequencyRepositoryPort frequencyRepo,
                                  ExceptionEntryRepositoryPort exceptionRepo) {
        this.routeRepo = routeRepo;
        this.routeStopsRepo = routeStopsRepo;
        this.frequencyRepo = frequencyRepo;
        this.exceptionRepo = exceptionRepo;
    }


    // --- Bygger ruteplan for en gitt dato ---
    protected void buildSchedule(LocalDate referenceDate) {
        allRoutes = routeRepo.readAll();
        List<RouteStops> allRouteStops = routeStopsRepo.readAll();

        for (Route route : allRoutes) {
            List<RouteStops> routeStops = new ArrayList<>();
            for (RouteStops rs : allRouteStops) {
                if (rs.getRoute() != null && rs.getRoute().getId() == route.getId()) {
                    routeStops.add(rs);
                }
            }
            routeStops.sort(Comparator.comparingInt(RouteStops::getRouteOrder));
            for (int i = 0; i < routeStops.size(); i++) routeStops.get(i).setRouteOrder(i + 1);
            route.setAllRouteStops(routeStops);
        }

        Weekday weekday = Weekday.fromLocalDate(referenceDate);
        scheduleMap.clear();

        for (Route route : allRoutes) {
            List<Frequency> activeFrequencies = frequencyRepo.findActiveForRouteAndDate(route, referenceDate);

            // Filtrer frekvenser utenfor aktiv sesong
            activeFrequencies.removeIf(freq -> freq.getSeason() != null &&
                    (freq.getSeason().getStartDate() != null && referenceDate.isBefore(freq.getSeason().getStartDate())
                            || freq.getSeason().getEndDate() != null && referenceDate.isAfter(freq.getSeason().getEndDate()))
            );

            if (activeFrequencies.isEmpty()) {
                activeFrequencies = frequencyRepo.findByRouteAndWeekday(route, weekday);
                activeFrequencies.removeIf(freq -> freq.getSeason() != null &&
                        (freq.getSeason().getStartDate() != null && referenceDate.isBefore(freq.getSeason().getStartDate())
                                || freq.getSeason().getEndDate() != null && referenceDate.isAfter(freq.getSeason().getEndDate()))
                );
            }

            Map<LocalTime, Frequency> uniqueFreq = new HashMap<>();
            for (Frequency freq : activeFrequencies) {
                for (LocalTime dep : freq.getDepartureTimes()) {
                    uniqueFreq.putIfAbsent(dep, freq);
                }
            }
            scheduleMap.put(route.getId(), new ArrayList<>(uniqueFreq.values()));
        }
    }

    // --- Hent avganger ---
    protected List<DepartureDTO> findDepartures(Stops fromStop, Stops toStop,
                                                LocalDate travelDate, LocalTime travelTime,
                                                TimeMode timeMode) {
        if (fromStop == null || toStop == null) return Collections.emptyList();

        // Bygg planen for dagen
        buildSchedule(travelDate);

        if (timeMode == TimeMode.NOW) {
            travelDate = LocalDate.now();
            travelTime = LocalTime.now();
        }

        Weekday weekday = Weekday.fromLocalDate(travelDate);
        List<DepartureDTO> departures = new ArrayList<>();

        for (Route route : allRoutes) {
            final Route currentRoute = route;
            final LocalDate dateForLambda = travelDate;

            if (!routeHasStopsInOrder(currentRoute, fromStop, toStop)) continue;

            List<Frequency> routeFrequencies = scheduleMap.getOrDefault(currentRoute.getId(), Collections.emptyList());

            Set<ExceptionEntry> activeExceptions = new HashSet<>();
            activeExceptions.addAll(exceptionRepo.findActiveForRouteAndDate(currentRoute, travelDate));
            activeExceptions.addAll(exceptionRepo.findActiveForRouteAndWeekday(currentRoute, weekday));
            activeExceptions.addAll(exceptionRepo.findActiveForStopAndDate(fromStop, travelDate).stream()
                    .filter(ex -> ex.getRoute() != null
                            && ex.getRoute().getId() == currentRoute.getId()
                            && isExceptionActiveForDateAndSeason(ex, dateForLambda))
                    .toList());
            activeExceptions.addAll(exceptionRepo.findActiveForStopAndWeekday(fromStop, weekday).stream()
                    .filter(ex -> ex.getRoute() != null
                            && ex.getRoute().getId() == currentRoute.getId()
                            && isExceptionActiveForDateAndSeason(ex, dateForLambda))
                    .toList());

            Set<LocalTime> addedDepartures = new HashSet<>();

            for (Frequency freq : routeFrequencies) {
                for (LocalTime firstStopDeparture : freq.getDepartureTimes()) {
                    int fromOffset = getMinutesFromStart(currentRoute, fromStop);
                    int toOffset = getMinutesFromStart(currentRoute, toStop);

                    LocalTime plannedDeparture = firstStopDeparture.plusMinutes(fromOffset);
                    LocalTime arrivalTime = firstStopDeparture.plusMinutes(toOffset);

                    if (addedDepartures.contains(plannedDeparture)) continue;

                    boolean skip = activeExceptions.stream()
                            .anyMatch(ex -> ex.affectsStop(fromStop)
                                    && ex.getDepartureTime().equals(firstStopDeparture)
                                    && (ex.isCancelled() || ex.isOmitted()));
                    if (skip) continue;

                    boolean timeSkip = false;

                    if (travelTime != null) { // bare sjekk hvis travelTime er satt
                        timeSkip = switch (timeMode) {
                            case DEPART -> plannedDeparture.isBefore(travelTime);
                            case ARRIVAL -> arrivalTime.isAfter(travelTime);
                            case NOW -> plannedDeparture.isBefore(travelTime);
                        };
                    }

                    if (timeSkip) continue; // hopp over denne avgangen hvis den ikke passer


                    String operationMessage = activeExceptions.stream()
                            .filter(ex -> ex.getDepartureTime().equals(firstStopDeparture))
                            .map(ex -> ex.getOperationMessage() != null ? ex.getOperationMessage().getMessage() : null)
                            .filter(Objects::nonNull)
                            .findFirst().orElse(null);

                    departures.add(createDepartureDTO(currentRoute, fromStop, toStop,
                            travelDate, plannedDeparture, arrivalTime, false, operationMessage));
                    addedDepartures.add(plannedDeparture);
                }
            }

            // Ekstraavganger
            for (ExceptionEntry ex : activeExceptions) {
                if (!ex.isExtra() || !ex.affectsStop(fromStop)) continue;
                if (ex.getRoute() == null || ex.getRoute().getId() != currentRoute.getId()) continue;

                int fromOffset = getMinutesFromStart(currentRoute, fromStop);
                int toOffset = getMinutesFromStart(currentRoute, toStop);

                LocalTime plannedDeparture = ex.getDepartureTime().plusMinutes(fromOffset);
                LocalTime arrivalTime = ex.getDepartureTime().plusMinutes(toOffset);

                if (addedDepartures.contains(plannedDeparture)) continue;

                boolean timeSkip = false;

                if (travelTime != null) { // bare sjekk hvis travelTime er satt
                    timeSkip = switch (timeMode) {
                        case DEPART -> plannedDeparture.isBefore(travelTime);
                        case ARRIVAL -> arrivalTime.isAfter(travelTime);
                        case NOW -> plannedDeparture.isBefore(travelTime);
                    };
                }

                if (timeSkip) continue; // hopp over denne avgangen hvis den ikke passer

                departures.add(createDepartureDTO(currentRoute, fromStop, toStop,
                        travelDate, plannedDeparture, arrivalTime, true,
                        ex.getOperationMessage() != null ? ex.getOperationMessage().getMessage() : null));
                addedDepartures.add(plannedDeparture);
            }
        }

        // Sorter korrekt
        if (timeMode == TimeMode.ARRIVAL) {
            departures.sort(Comparator.comparing(DepartureDTO::getArrivalTime).reversed());
        } else {
            departures.sort(Comparator.comparing(DepartureDTO::getPlannedDeparture));
        }

        return departures;
    }

    private boolean isExceptionActiveForDateAndSeason(ExceptionEntry ex, LocalDate date) {
        if (ex.getSeason() == null) return true;
        LocalDate start = ex.getSeason().getStartDate();
        LocalDate end = ex.getSeason().getEndDate();
        return (start == null || !date.isBefore(start)) && (end == null || !date.isAfter(end));
    }

    protected boolean routeHasStopsInOrder(Route route, Stops fromStop, Stops toStop) {
        List<RouteStops> stops = route.getStops();
        int fromIndex = -1, toIndex = -1;
        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i).getStop().getId() == fromStop.getId()) fromIndex = i;
            if (stops.get(i).getStop().getId() == toStop.getId()) toIndex = i;
        }
        return fromIndex >= 0 && toIndex >= 0 && fromIndex < toIndex;
    }

    protected int getMinutesFromStart(Route route, Stops stop) {
        return route.getStops().stream()
                .filter(rs -> rs.getStop().getId() == stop.getId())
                .findFirst()
                .map(RouteStops::getTimeFromStart)
                .orElse(0);
    }

    protected DepartureDTO createDepartureDTO(Route route, Stops fromStop, Stops toStop,
                                              LocalDate travelDate, LocalTime plannedDeparture,
                                              LocalTime arrivalTime, boolean isExtra, String operationMessage) {
        DepartureDTO dto = new DepartureDTO(
                route.getId(),
                fromStop.getId(),
                fromStop.getName(),
                toStop.getId(),
                toStop.getName(),
                travelDate,
                plannedDeparture,
                arrivalTime,
                isExtra,
                false,
                false,
                false,
                0,
                operationMessage
        );
        dto.setRouteNumber(route.getRouteNum());
        return dto;
    }

}
