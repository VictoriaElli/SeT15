package unitTesting;

import domain.model.Route;
import domain.model.Stops;
import domain.model.TimeMode;
import dto.DepartureDTO;
import dto.DepartureRequestDTO;
import dto.DepartureResponseDTO;
import dto.ScheduleDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import port.outbound.*;
import service.EnvironmentService;
import service.ScheduleService;

import java.time.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleServiceUnitTests {

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

    @Test
    void testGetFullSchedule_onlyFutureDepartures() {
        // Sett opp stoppene
        Stops fromStop = new Stops(1, "Gamlebyen");
        Stops toStop = new Stops(2, "Ålekilen");

        // Lag en rute
        Route route = new Route(100, fromStop, toStop, true);

        // Lag fixed clock
        Clock fixedClock = Clock.fixed(Instant.parse("2025-11-20T08:00:00Z"), ZoneId.systemDefault());
        LocalTime now = LocalTime.now(fixedClock);
        LocalDate today = LocalDate.now(fixedClock);

        // Lag avgangene – én i fortiden, én i fremtiden
        DepartureDTO pastDep = new DepartureDTO();
        pastDep.setRouteId(100);
        pastDep.setRouteNumber(100);
        pastDep.setFromStopId(1);
        pastDep.setFromStopName("Gamlebyen");
        pastDep.setToStopId(2);
        pastDep.setToStopName("Ålekilen");
        pastDep.setTravelDate(today);
        pastDep.setPlannedDeparture(now.minusHours(1));
        pastDep.setArrivalTime(now.plusHours(1));

        DepartureDTO futureDep = new DepartureDTO();
        futureDep.setRouteId(100);
        futureDep.setRouteNumber(100);
        futureDep.setFromStopId(1);
        futureDep.setFromStopName("Gamlebyen");
        futureDep.setToStopId(2);
        futureDep.setToStopName("Ålekilen");
        futureDep.setTravelDate(today);
        futureDep.setPlannedDeparture(now.plusHours(1));
        futureDep.setArrivalTime(now.plusHours(2));

        // Lag en subklasse av ScheduleService for å override findDepartures
        class TestScheduleService extends ScheduleService {
            private final List<DepartureDTO> mockDepartures;

            public TestScheduleService(RouteRepositoryPort routeRepo,
                                       RouteStopsRepositoryPort routeStopsRepo,
                                       FrequencyRepositoryPort frequencyRepo,
                                       ExceptionEntryRepositoryPort exceptionRepo,
                                       StopsRepositoryPort stopsRepo,
                                       EnvironmentService environmentService,
                                       List<DepartureDTO> mockDepartures) {
                super(routeRepo, routeStopsRepo, frequencyRepo, exceptionRepo, stopsRepo, environmentService);
                this.mockDepartures = mockDepartures;
            }

            @Override
            protected List<DepartureDTO> findDepartures(Stops from, Stops to, LocalDate date, LocalTime time) {
                return mockDepartures;
            }
        }

        // Opprett testservice
        List<DepartureDTO> mockDepartures = List.of(pastDep, futureDep);
        TestScheduleService testService = new TestScheduleService(
                routeRepo,
                routeStopsRepo,
                frequencyRepo,
                exceptionRepo,
                stopsRepo,
                environmentService,
                mockDepartures
        );

        testService.setClock(fixedClock);
        testService.allRoutes = List.of(route);

        // Kall metoden
        List<ScheduleDTO> result = testService.getFullSchedule(today);

        // Sjekk at kun fremtidige avganger returneres
        assertEquals(1, result.size());
        ScheduleDTO dto = result.get(0);
        assertEquals(100, dto.getRouteNumber());
        assertEquals(1, dto.getPlannedDepartures().size());
        assertEquals(futureDep.getPlannedDeparture(), dto.getPlannedDepartures().get(0));
    }

    @Test
    void testGetDepartures_timeModeNow_filtersPastDepartures() {
        Stops fromStop = new Stops(1, "Gamlebyen");
        Stops toStop = new Stops(2, "Ålekilen");

        when(stopsRepo.readAll()).thenReturn(List.of(fromStop, toStop));

        // Lag fixed clock
        Clock fixedClock = Clock.fixed(Instant.parse("2025-11-20T08:00:00Z"), ZoneId.systemDefault());
        LocalDate travelDate = LocalDate.now(fixedClock);
        LocalTime now = LocalTime.now(fixedClock);

        // Lag avgangene – én i fortiden, én i fremtiden
        DepartureDTO pastDep = new DepartureDTO();
        pastDep.setRouteId(100);
        pastDep.setRouteNumber(100);
        pastDep.setFromStopId(1);
        pastDep.setFromStopName("Gamlebyen");
        pastDep.setToStopId(2);
        pastDep.setToStopName("Ålekilen");
        pastDep.setTravelDate(travelDate);
        pastDep.setPlannedDeparture(now.minusHours(1));
        pastDep.setArrivalTime(now.plusHours(1));

        DepartureDTO futureDep = new DepartureDTO();
        futureDep.setRouteId(100);
        futureDep.setRouteNumber(100);
        futureDep.setFromStopId(1);
        futureDep.setFromStopName("Gamlebyen");
        futureDep.setToStopId(2);
        futureDep.setToStopName("Ålekilen");
        futureDep.setTravelDate(travelDate);
        futureDep.setPlannedDeparture(now.plusHours(1));
        futureDep.setArrivalTime(now.plusHours(2));

        // Lag en subklasse av ScheduleService for å override findDepartures
        class TestScheduleService extends ScheduleService {
            private final List<DepartureDTO> mockDepartures;

            public TestScheduleService(RouteRepositoryPort routeRepo,
                                       RouteStopsRepositoryPort routeStopsRepo,
                                       FrequencyRepositoryPort frequencyRepo,
                                       ExceptionEntryRepositoryPort exceptionRepo,
                                       StopsRepositoryPort stopsRepo,
                                       EnvironmentService environmentService,
                                       List<DepartureDTO> mockDepartures) {
                super(routeRepo, routeStopsRepo, frequencyRepo, exceptionRepo, stopsRepo, environmentService);
                this.mockDepartures = mockDepartures;
            }

            @Override
            protected List<DepartureDTO> findDepartures(Stops from, Stops to, LocalDate date, LocalTime time) {
                return mockDepartures;
            }
        }

        List<DepartureDTO> mockDepartures = List.of(pastDep, futureDep);
        TestScheduleService testService = new TestScheduleService(
                routeRepo,
                routeStopsRepo,
                frequencyRepo,
                exceptionRepo,
                stopsRepo,
                environmentService,
                mockDepartures
        );

        testService.setClock(fixedClock);

        DepartureRequestDTO request = new DepartureRequestDTO(
                "Gamlebyen", "Ålekilen", travelDate, null, TimeMode.NOW
        );

        List<DepartureResponseDTO> result = testService.getDepartures(request);

        // Sjekk at bare fremtidige avganger returneres
        assertEquals(1, result.size());
        assertEquals(futureDep.getPlannedDeparture(), result.get(0).getPlannedDeparture());
    }

}

