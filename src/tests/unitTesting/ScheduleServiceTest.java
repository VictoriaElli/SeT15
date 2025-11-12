package domain.service;

import domain.model.*;
import domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ScheduleServiceTest {

    private RouteRepository routeRepository;
    private FrequencyRepository frequencyRepository;
    private ExceptionEntryRepository exceptionRepository;
    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        routeRepository = mock(RouteRepository.class);
        frequencyRepository = mock(FrequencyRepository.class);
        exceptionRepository = mock(ExceptionEntryRepository.class);
        scheduleService = new ScheduleService(routeRepository, frequencyRepository, exceptionRepository);
    }

    @Test
    void updateSchedule_ShouldUpdateFrequenciesAndExceptions() {
        // Arrange
        int routeId = 1;
        LocalDate date = LocalDate.of(2025, 11, 12);

        Route route = new Route(routeId, 101);

        Frequency freq = new Frequency(10, route, Weekday.MONDAY, new Season("Winter", 2025,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31)),
                LocalTime.of(6, 0), LocalTime.of(10, 0), 30);

        ExceptionEntry exc = new ExceptionEntry.Builder()
                .setId(20)
                .setRoute(route)
                .setValidDate(date)
                .setDepartureTime(LocalTime.of(8, 0))
                .setType(ExceptionType.DELAYED)
                .build();

        when(routeRepository.read(routeId)).thenReturn(route);
        when(frequencyRepository.readAll()).thenReturn(List.of(freq));
        when(exceptionRepository.readAll()).thenReturn(List.of(exc));

        // Act
        Schedule result = scheduleService.updateSchedule(routeId, date, List.of(freq), List.of(exc));

        // Assert
        verify(frequencyRepository).update(freq);
        verify(exceptionRepository).update(exc);
        assertNotNull(result);
        assertEquals(routeId, result.getRoute().getId());
        assertEquals(1, result.getFrequencies().size());
        assertEquals(1, result.getExceptions().size());
    }

    @Test
    void updateSchedule_ShouldThrowException_WhenRouteNotFound() {
        int routeId = 99;
        LocalDate date = LocalDate.of(2025, 11, 12);

        when(routeRepository.read(routeId)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                scheduleService.updateSchedule(routeId, date, List.of(), List.of())
        );
        assertTrue(ex.getMessage().contains("Route not found"));
    }

    @Test
    void updateSchedule_ShouldThrowException_WhenFrequencyBelongsToWrongRoute() {
        int routeId = 1;
        LocalDate date = LocalDate.of(2025, 11, 12);

        Route correctRoute = new Route(routeId, 101);
        Route wrongRoute = new Route(2, 202);

        Frequency wrongFreq = new Frequency(10, wrongRoute, Weekday.MONDAY, new Season("Winter", 2025,
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 3, 31)),
                LocalTime.of(6, 0), LocalTime.of(10, 0), 30);

        when(routeRepository.read(routeId)).thenReturn(correctRoute);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                scheduleService.updateSchedule(routeId, date, List.of(wrongFreq), List.of())
        );
        assertTrue(ex.getMessage().contains("Frequency does not belong"));
    }

    @Test
    void updateSchedule_ShouldThrowException_WhenExceptionBelongsToWrongRoute() {
        int routeId = 1;
        LocalDate date = LocalDate.of(2025, 11, 12);

        Route correctRoute = new Route(routeId, 101);
        Route wrongRoute = new Route(2, 202);

        ExceptionEntry wrongExc = new ExceptionEntry.Builder()
                .setId(30)
                .setRoute(wrongRoute)
                .setValidDate(date)
                .setDepartureTime(LocalTime.of(9, 0))
                .setType(ExceptionType.CANCELLED)
                .build();

        when(routeRepository.read(routeId)).thenReturn(correctRoute);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                scheduleService.updateSchedule(routeId, date, List.of(), List.of(wrongExc))
        );
        assertTrue(ex.getMessage().contains("Exception does not belong"));
    }

    @Test
    void updateSchedule_ShouldHandleEmptyLists() {
        int routeId = 1;
        LocalDate date = LocalDate.of(2025, 11, 12);

        Route route = new Route(routeId, 101);

        when(routeRepository.read(routeId)).thenReturn(route);
        when(frequencyRepository.readAll()).thenReturn(List.of());
        when(exceptionRepository.readAll()).thenReturn(List.of());

        Schedule result = scheduleService.updateSchedule(routeId, date, List.of(), List.of());

        assertNotNull(result);
        assertEquals(routeId, result.getRoute().getId());
        assertTrue(result.getFrequencies().isEmpty());
        assertTrue(result.getExceptions().isEmpty());
    }
}