package service;

import domain.model.*;
import dto.DepartureDTO;
import port.outbound.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class ScheduleServiceWithoutDTO {

    private final RouteRepositoryPort routeRepo;
    private final RouteStopsRepositoryPort routeStopsRepo;
    private final FrequencyRepositoryPort frequencyRepo;
    private final ExceptionEntryRepositoryPort exceptionRepo;

    private List<Route> allRoutes = new ArrayList<>();
    private Map<Integer, List<Frequency>> scheduleMap = new HashMap<>();

    public ScheduleServiceWithoutDTO(RouteRepositoryPort routeRepo,
                                     RouteStopsRepositoryPort routeStopsRepo,
                                     FrequencyRepositoryPort frequencyRepo,
                                     ExceptionEntryRepositoryPort exceptionRepo) {
        this.routeRepo = routeRepo;
        this.routeStopsRepo = routeStopsRepo;
        this.frequencyRepo = frequencyRepo;
        this.exceptionRepo = exceptionRepo;
    }

    /** Bygger tidstabeller for alle ruter med frekvens og sesongfiltrering */
    public void buildSchedule(LocalDate referenceDate) {
        allRoutes = routeRepo.readAll();
        List<RouteStops> allRouteStops = routeStopsRepo.readAll();

        // Sett stops per rute
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
            if (activeFrequencies.isEmpty()) {
                activeFrequencies = frequencyRepo.findByRouteAndWeekday(route, weekday);
            }

            // Fjern duplikater
            Map<LocalTime, Frequency> uniqueFreq = new HashMap<>();
            for (Frequency freq : activeFrequencies) {
                for (LocalTime dep : freq.getDepartureTimes()) {
                    uniqueFreq.putIfAbsent(dep, freq);
                }
            }
            scheduleMap.put(route.getId(), new ArrayList<>(uniqueFreq.values()));
        }
    }

    /**
     * Hent avganger med TimeMode støtte.
     */
    public List<DepartureDTO> getDepartures(Stops fromStop, Stops toStop,
                                            LocalDate travelDate, LocalTime travelTime,
                                            TimeMode timeMode) {
        if (fromStop == null || toStop == null) return Collections.emptyList();

        // Oppdater schedule for valgt dato
        buildSchedule(travelDate);

        if (timeMode == TimeMode.NOW) {
            travelDate = LocalDate.now();
            travelTime = LocalTime.now();
        }

        Weekday weekday = Weekday.fromLocalDate(travelDate);
        List<DepartureDTO> departures = new ArrayList<>();

        for (Route route : allRoutes) {
            if (!routeHasStopsInOrder(route, fromStop, toStop)) continue;

            List<Frequency> routeFrequencies = scheduleMap.getOrDefault(route.getId(), Collections.emptyList());

            // Hent exception entries
            Set<ExceptionEntry> activeExceptions = new HashSet<>();
            activeExceptions.addAll(exceptionRepo.findActiveForRouteAndDate(route, travelDate));
            activeExceptions.addAll(exceptionRepo.findActiveForRouteAndWeekday(route, weekday));
            activeExceptions.addAll(exceptionRepo.findActiveForStopAndDate(fromStop, travelDate));
            activeExceptions.addAll(exceptionRepo.findActiveForStopAndWeekday(fromStop, weekday));

            Set<LocalTime> addedDepartures = new HashSet<>();

            // Ordinære avganger
            for (Frequency freq : routeFrequencies) {
                for (LocalTime firstStopDeparture : freq.getDepartureTimes()) {
                    int minutesFromStart = getMinutesFromStart(route, fromStop);
                    LocalTime plannedDeparture = firstStopDeparture.plusMinutes(minutesFromStart);

                    if (addedDepartures.contains(plannedDeparture)) continue;

                    // Sjekk for cancel/omitted
                    boolean skip = activeExceptions.stream()
                            .anyMatch(ex -> ex.affectsStop(fromStop) &&
                                    ex.getDepartureTime().equals(firstStopDeparture) &&
                                    (ex.isCancelled() || ex.isOmitted()));
                    if (skip) continue;

                    addedDepartures.add(plannedDeparture);

                    // Beregn korrekt arrivalTime mellom fromStop og toStop
                    LocalTime arrivalTime = calculateArrivalTime(route, fromStop, toStop, firstStopDeparture);

                    boolean timeSkip = switch (timeMode) {
                        case DEPART -> plannedDeparture.isBefore(travelTime);
                        case ARRIVAL -> arrivalTime.isAfter(travelTime);
                        case NOW -> plannedDeparture.isBefore(travelTime);
                    };
                    if (timeSkip) continue;

                    String operationMessage = activeExceptions.stream()
                            .filter(ex -> ex.getDepartureTime().equals(firstStopDeparture))
                            .map(ex -> ex.getOperationMessage() != null ? ex.getOperationMessage().getMessage() : null)
                            .filter(Objects::nonNull)
                            .findFirst().orElse(null);

                    departures.add(createDepartureDTO(route, fromStop, toStop,
                            travelDate, plannedDeparture, arrivalTime, false, operationMessage));
                }
            }

            // Ekstraavganger
            for (ExceptionEntry ex : activeExceptions) {
                if (!ex.isExtra() || !ex.affectsStop(fromStop)) continue;

                LocalTime firstStopDeparture = ex.getDepartureTime();
                int minutesFromStart = getMinutesFromStart(route, fromStop);
                LocalTime plannedDeparture = firstStopDeparture.plusMinutes(minutesFromStart);
                if (addedDepartures.contains(plannedDeparture)) continue;

                LocalTime arrivalTime = calculateArrivalTime(route, fromStop, toStop, firstStopDeparture);
                boolean timeSkip = switch (timeMode) {
                    case DEPART -> plannedDeparture.isBefore(travelTime);
                    case ARRIVAL -> arrivalTime.isAfter(travelTime);
                    case NOW -> plannedDeparture.isBefore(travelTime);
                };
                if (timeSkip) continue;

                departures.add(createDepartureDTO(route, fromStop, toStop,
                        travelDate, plannedDeparture, arrivalTime, true,
                        ex.getOperationMessage() != null ? ex.getOperationMessage().getMessage() : null));
                addedDepartures.add(plannedDeparture);
            }
        }

        // Sortering
        if (timeMode == TimeMode.ARRIVAL) {
            departures.sort(Comparator.comparing(DepartureDTO::getArrivalTime).reversed());
        } else {
            departures.sort(Comparator.comparing(DepartureDTO::getPlannedDeparture));
        }

        return departures;
    }

    // --- Hjelpemetoder ---
    private int getMinutesFromStart(Route route, Stops stop) {
        return route.getStops().stream()
                .filter(rs -> rs.getStop().getId() == stop.getId())
                .findFirst()
                .map(RouteStops::getTimeFromStart)
                .orElse(0);
    }

    private boolean routeHasStopsInOrder(Route route, Stops fromStop, Stops toStop) {
        List<RouteStops> stops = route.getStops();
        int fromIndex = -1, toIndex = -1;
        for (int i = 0; i < stops.size(); i++) {
            if (stops.get(i).getStop().getId() == fromStop.getId()) fromIndex = i;
            if (stops.get(i).getStop().getId() == toStop.getId()) toIndex = i;
        }
        return fromIndex >= 0 && toIndex >= 0 && fromIndex < toIndex;
    }

    private LocalTime calculateArrivalTime(Route route, Stops fromStop, Stops toStop, LocalTime firstStopDeparture) {
        int fromMinutes = getMinutesFromStart(route, fromStop);
        int toMinutes = getMinutesFromStart(route, toStop);
        return firstStopDeparture.plusMinutes(toMinutes - fromMinutes);
    }

    private boolean isInSeason(Frequency freq, LocalDate date) {
        return freq.getSeason() == null || freq.getSeason().isActiveOn(date);
    }

    private DepartureDTO createDepartureDTO(Route route, Stops fromStop, Stops toStop,
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
                false, // isDelayed
                false, // isCancelled
                false, // isOmitted
                0,     // delayMinutes
                operationMessage
        );
        dto.setRouteNumber(route.getRouteNum());
        return dto;
    }

    // --- CRUD-metoder ---
    public void createRoute(Route route) { routeRepo.create(route); }
    public void updateRoute(Route route) { routeRepo.update(route); }
    public void deleteRoute(Route route) { routeRepo.delete(route); }
    public Route readRouteById(int id) { return routeRepo.readById(id).orElse(null); }
    public List<Route> readAllRoutes() { return routeRepo.readAll(); }

    public void createFrequency(Frequency freq) { frequencyRepo.create(freq); }
    public void updateFrequency(Frequency freq) { frequencyRepo.update(freq); }
    public void deleteFrequency(Frequency freq) { frequencyRepo.delete(freq); }
    public Frequency readFrequencyById(int id) { return frequencyRepo.readById(id).orElse(null); }
    public List<Frequency> readAllFrequencies() { return frequencyRepo.readAll(); }

    public void createException(ExceptionEntry ex) { exceptionRepo.create(ex); }
    public void updateException(ExceptionEntry ex) { exceptionRepo.update(ex); }
    public void deleteException(ExceptionEntry ex) { exceptionRepo.delete(ex); }
    public ExceptionEntry readExceptionById(int id) { return exceptionRepo.readById(id).orElse(null); }
    public List<ExceptionEntry> readAllExceptions() { return exceptionRepo.readAll(); }
}
