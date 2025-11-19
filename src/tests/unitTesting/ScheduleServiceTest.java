package unitTesting;

import domain.model.Stops;
import dto.DepartureRequestDTO;
import dto.DepartureResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import port.outbound.*;
import service.EnvironmentService;
import service.ScheduleService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleServiceTest {

    private RouteRepositoryPort routeRepo;
    private RouteStopsRepositoryPort routeStopsRepo;
    private FrequencyRepositoryPort frequencyRepo;
    private ExceptionEntryRepositoryPort exceptionRepo;
    private StopsRepositoryPort stopsRepo;
    private EnvironmentService environmentService;

    private ScheduleService scheduleService;

    @BeforeEach
    void setup() {
        routeRepo = mock(RouteRepositoryPort.class);
        routeStopsRepo = mock(RouteStopsRepositoryPort.class);
        frequencyRepo = mock(FrequencyRepositoryPort.class);
        exceptionRepo = mock(ExceptionEntryRepositoryPort.class);
        stopsRepo = mock(StopsRepositoryPort.class);
        environmentService = mock(EnvironmentService.class);

        scheduleService = new ScheduleService(
                routeRepo,
                routeStopsRepo,
                frequencyRepo,
                exceptionRepo,
                stopsRepo,
                environmentService
        );
    }

    @Test
    void testGetDepartures_validStops_returnsList() {
        Stops fromStop = new Stops(1, "Gamlebyen");
        Stops toStop = new Stops(2, "Ålekilen");

        when(stopsRepo.readAll()).thenReturn(List.of(fromStop, toStop));
        when(environmentService.calculateSavings(anyInt(), anyInt()))
                .thenReturn(null); // Enkel mock for miljøberegning

        DepartureRequestDTO request = new DepartureRequestDTO(
                "Gamlebyen",
                "Ålekilen",
                LocalDate.now(),
                LocalTime.of(10, 0),
                null
        );

        List<DepartureResponseDTO> result = scheduleService.getDepartures(request);

        assertNotNull(result);
        // Vi forventer tom liste siden vi ikke mocker noen ruter/frekvenser
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetDepartures_fromStopNotFound_returnsEmptyList() {
        Stops toStop = new Stops(2, "Ålekilen");

        when(stopsRepo.readAll()).thenReturn(List.of(toStop));

        DepartureRequestDTO request = new DepartureRequestDTO(
                "Gamlebyen", "Ålekilen",
                LocalDate.now(),
                LocalTime.of(10, 0),
                null
        );

        List<DepartureResponseDTO> result = scheduleService.getDepartures(request);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetDepartures_toStopNotFound_returnsEmptyList() {
        Stops fromStop = new Stops(1, "Gamlebyen");

        when(stopsRepo.readAll()).thenReturn(List.of(fromStop));

        DepartureRequestDTO request = new DepartureRequestDTO(
                "Gamlebyen", "Ålekilen",
                LocalDate.now(),
                LocalTime.of(10, 0),
                null
        );

        List<DepartureResponseDTO> result = scheduleService.getDepartures(request);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetDepartures_environmentThrowsException_setsNull() {
        Stops fromStop = new Stops(1, "Gamlebyen");
        Stops toStop = new Stops(2, "Ålekilen");

        when(stopsRepo.readAll()).thenReturn(List.of(fromStop, toStop));
        when(environmentService.calculateSavings(anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Env error"));

        DepartureRequestDTO request = new DepartureRequestDTO(
                "Gamlebyen",
                "Ålekilen",
                LocalDate.now(),
                LocalTime.of(10, 0),
                null
        );

        List<DepartureResponseDTO> result = scheduleService.getDepartures(request);

        // Vi forventer fortsatt en liste, men environmentSavings skal være null
        assertNotNull(result);
        result.forEach(dto -> assertNull(dto.getEnvironmentSavings()));
    }
}

