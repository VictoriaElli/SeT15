package unitTesting;

import domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import port.outbound.ExceptionEntryRepository;
import port.outbound.FrequencyRepository;
import usecases.ScheduleService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class ScheduleServiceTest {

    private FrequencyRepository frequencyRepo;
    private ExceptionEntryRepository exceptionRepo;
    private ScheduleService scheduleService;

    private Route route;
    private Stop stop;

    @BeforeEach
    void setup() {
        // Lag mock-objekter
        frequencyRepo = mock(FrequencyRepository.class);
        exceptionRepo = mock(ExceptionEntryRepository.class);

        // Sett inn mockene i ScheduleService
        scheduleService = new ScheduleService(frequencyRepo, exceptionRepo);

        // Eksempeldata
        route = new Route(1, 111);
        stop = new Stop(1, "Gamlebyen");
    }

    @Test
    void testGenerateTimes_basicFrequency() {
        Season season = new Season("Vinter", 2025,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 3, 31));

        Frequency frequency = new Frequency(1, route, Weekday.MONDAY, season,
                LocalTime.of(8, 0), LocalTime.of(10, 0), 30);

        LocalDate date = LocalDate.of(2025, 1, 6); // Mandag

        List<LocalTime> times = scheduleService.generateTimes(frequency, date);

        List<LocalTime> expected = List.of(
                LocalTime.of(8, 0),
                LocalTime.of(8, 30),
                LocalTime.of(9, 0),
                LocalTime.of(9, 30),
                LocalTime.of(10, 0)
        );

        assertEquals(expected, times);
    }

    @Test
    void testGenerateTimesForDate_withExceptions() {
        LocalDate date = LocalDate.of(2025, 1, 6); // Mandag
        Season season = new Season("Vinter", 2025,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 3, 31));

        Frequency frequency = new Frequency(1, route, Weekday.MONDAY, season,
                LocalTime.of(8, 0), LocalTime.of(10, 0), 30);

        // Mock repository-kall
        when(frequencyRepo.findByRouteAndStop(route, stop)).thenReturn(List.of(frequency));

        ExceptionEntry cancelEx = new ExceptionEntry(route, stop, date, season,
                LocalTime.of(9, 0), ExceptionType.CANCELLED, null);
        ExceptionEntry addEx = new ExceptionEntry(route, stop, date, season,
                LocalTime.of(9, 15), ExceptionType.EXTRA, null);

        when(exceptionRepo.findByRouteStopAndDate(route, stop, date))
                .thenReturn(List.of(cancelEx, addEx));

        List<LocalTime> times = scheduleService.generateTimesForDate(route, stop, date);

        List<LocalTime> expected = List.of(
                LocalTime.of(8, 0),
                LocalTime.of(8, 30),
                LocalTime.of(9, 15),
                LocalTime.of(9, 30),
                LocalTime.of(10, 0)
        );

        assertEquals(expected, times);

        // Verifiser at metodene ble kalt
        verify(frequencyRepo).findByRouteAndStop(route, stop);
        verify(exceptionRepo).findByRouteStopAndDate(route, stop, date);
    }

    @Test
    void testGenerateTimesForDate_withExceptions_debug() {
        LocalDate date = LocalDate.of(2025, 1, 6); // Mandag
        Season season = new Season("Vinter", 2025,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 3, 31));

        Frequency frequency = new Frequency(1, route, Weekday.MONDAY, season,
                LocalTime.of(8, 0), LocalTime.of(10, 0), 30);

        // Mock repository-kall
        when(frequencyRepo.findByRouteAndStop(route, stop)).thenReturn(List.of(frequency));

        ExceptionEntry cancelEx = new ExceptionEntry(route, stop, date, season,
                LocalTime.of(9, 0), ExceptionType.CANCELLED, null);
        ExceptionEntry addEx = new ExceptionEntry(route, stop, date, season,
                LocalTime.of(9, 15), ExceptionType.EXTRA, null);

        when(exceptionRepo.findByRouteStopAndDate(route, stop, date))
                .thenReturn(List.of(cancelEx, addEx));

        List<LocalTime> times = scheduleService.generateTimesForDate(route, stop, date);

        // Debug info
        System.out.println("=== DEBUG INFO ===");
        System.out.println("Date: " + date);
        System.out.println("Weekday from date: " + Weekday.fromLocalDate(date));
        System.out.println("Season active: " + season.isActiveOn(date));
        System.out.println("Generated times: " + times);

        List<LocalTime> expected = List.of(
                LocalTime.of(8, 0),
                LocalTime.of(8, 30),
                LocalTime.of(9, 15),
                LocalTime.of(9, 30),
                LocalTime.of(10, 0)
        );

        assertEquals(expected, times);
    }

}
