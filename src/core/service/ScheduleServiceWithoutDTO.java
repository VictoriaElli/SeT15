package service;

import domain.model.*;
import dto.DepartureDTO;
import org.springframework.stereotype.Service;
import port.outbound.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class ScheduleServiceWithoutDTO extends BaseScheduleService {

    public ScheduleServiceWithoutDTO(RouteRepositoryPort routeRepo,
                                     RouteStopsRepositoryPort routeStopsRepo,
                                     FrequencyRepositoryPort frequencyRepo,
                                     ExceptionEntryRepositoryPort exceptionRepo) {
        super(routeRepo, routeStopsRepo, frequencyRepo, exceptionRepo);
    }

    public List<DepartureDTO> getDepartures(Stops fromStop, Stops toStop,
                                            LocalDate travelDate, LocalTime travelTime,
                                            TimeMode timeMode) {
        return findDepartures(fromStop, toStop, travelDate, travelTime, timeMode);
    }
}
