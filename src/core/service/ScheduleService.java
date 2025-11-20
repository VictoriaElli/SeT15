package service;

import domain.model.*;
import dto.DepartureDTO;
import dto.DepartureRequestDTO;
import dto.DepartureResponseDTO;
import dto.ScheduleDTO;
import org.springframework.stereotype.Service;
import port.outbound.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleService extends BaseScheduleService {

    private final StopsRepositoryPort stopsRepo;
    private final EnvironmentService environmentService;

    public ScheduleService(RouteRepositoryPort routeRepo,
                           RouteStopsRepositoryPort routeStopsRepo,
                           FrequencyRepositoryPort frequencyRepo,
                           ExceptionEntryRepositoryPort exceptionRepo,
                           StopsRepositoryPort stopsRepo, EnvironmentService environmentService) {
        super(routeRepo, routeStopsRepo, frequencyRepo, exceptionRepo);
        this.stopsRepo = stopsRepo;
        this.environmentService = environmentService;
    }

    public List<DepartureResponseDTO> getDepartures(DepartureRequestDTO request) {
        List<Stops> allStops = stopsRepo.readAll();

        Stops fromStop = allStops.stream()
                .filter(s -> s.getName().trim().equalsIgnoreCase(request.getFromStop().trim()))
                .findFirst()
                .orElse(null);

        Stops toStop = allStops.stream()
                .filter(s -> s.getName().trim().equalsIgnoreCase(request.getToStop().trim()))
                .findFirst()
                .orElse(null);

        if (fromStop == null) {
            System.err.println("From stop not found: " + request.getFromStop());
            return Collections.emptyList();
        }
        if (toStop == null) {
            System.err.println("To stop not found: " + request.getToStop());
            return Collections.emptyList();
        }

        List<DepartureDTO> departures = findDepartures(fromStop, toStop,
                request.getTravelDate(), request.getTravelTime(), request.getTimeMode());

        List<DepartureResponseDTO> response = new ArrayList<>();
        for (DepartureDTO dto : departures) {
            DepartureResponseDTO respDTO = new DepartureResponseDTO(dto, request.getTimeMode());

            // Hent miljødata
            try {
                respDTO.setEnvironmentSavings(environmentService.calculateSavings(fromStop.getId(), toStop.getId()));
            } catch (RuntimeException e) {
                respDTO.setEnvironmentSavings(null); // <- her settes det null hvis route == null
            }


            response.add(respDTO);
        }
        return response;
    }

    public List<ScheduleDTO> getFullSchedule(LocalDate date) {
        // Bygg planen for dagen
        buildSchedule(date);

        Map<String, ScheduleDTO> scheduleMap = new HashMap<>();

        for (Route route : allRoutes) {
            Stops fromStop = route.getFromStop();
            Stops toStop = route.getToStop();

            if (fromStop == null || toStop == null) continue;

            // Hent alle avganger fra ruten, kun fra første stopp
            List<DepartureDTO> departures = findDepartures(fromStop, toStop, date, null, TimeMode.DEPART)
                    .stream()
                    .filter(dep -> dep.getFromStopName() != null && dep.getFromStopName().equals(fromStop.getName()))
                    .collect(Collectors.toList());

            if (departures.isEmpty()) continue;

            // Lag nøkkel per rute/retning
            String key = route.getRouteNum() + "_" + fromStop.getName() + "_" + toStop.getName();

            ScheduleDTO dto = scheduleMap.get(key);
            if (dto == null) {
                dto = new ScheduleDTO(
                        route.getRouteNum(),
                        fromStop.getName(),
                        toStop.getName(),
                        new ArrayList<>()
                );
                scheduleMap.put(key, dto);
            }

            // Legg til kun avgangstidene fra første stopp
            for (DepartureDTO dep : departures) {
                dto.getPlannedDepartures().add(dep.getPlannedDeparture());
            }

            // Sorter stigende
            dto.getPlannedDepartures().sort(Comparator.naturalOrder());
        }

        List<ScheduleDTO> list = new ArrayList<>(scheduleMap.values());

        list.sort(Comparator.comparingInt(ScheduleDTO::getRouteNumber).reversed());

        return list;
    }


}
