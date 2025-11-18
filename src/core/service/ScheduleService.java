package service;

import domain.model.*;
import dto.DepartureDTO;
import dto.DepartureRequestDTO;
import dto.DepartureResponseDTO;
import org.springframework.stereotype.Service;
import port.outbound.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ScheduleService extends BaseScheduleService {

    private final StopsRepositoryPort stopsRepo;

    public ScheduleService(RouteRepositoryPort routeRepo,
                           RouteStopsRepositoryPort routeStopsRepo,
                           FrequencyRepositoryPort frequencyRepo,
                           ExceptionEntryRepositoryPort exceptionRepo,
                           StopsRepositoryPort stopsRepo) {
        super(routeRepo, routeStopsRepo, frequencyRepo, exceptionRepo);
        this.stopsRepo = stopsRepo;
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
            response.add(new DepartureResponseDTO(dto, request.getTimeMode()));
        }
        return response;
    }

}
