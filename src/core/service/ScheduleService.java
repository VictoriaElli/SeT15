package service;

import adapter.ExceptionEntryRepositoryPortMYSQLAdapter;
import adapter.FrequencyRepositoryPortMYSQLAdapter;
import domain.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;

public class ScheduleService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    private final FrequencyRepositoryPortMYSQLAdapter frequencyRepositoryMYSQLAdapter;
    private final ExceptionEntryRepositoryPortMYSQLAdapter exceptionEntryRepositoryMYSQLAdapter;

    @Autowired
    public ScheduleService(FrequencyRepositoryPortMYSQLAdapter frequencyRepositoryMYSQLAdapter, ExceptionEntryRepositoryPortMYSQLAdapter exceptionEntryRepositoryMYSQLAdapter) {
        this.frequencyRepositoryMYSQLAdapter = frequencyRepositoryMYSQLAdapter;
        this.exceptionEntryRepositoryMYSQLAdapter = exceptionEntryRepositoryMYSQLAdapter;
    }

    /**
     * Henter tidsplanen for en gitt rute og dato, inkludert både frekvenser og unntak.
     */
    public Schedule getSchedule(Route route, LocalDate date) {
        // Sjekk for gyldige parametere
        if (route == null || date == null) {
            throw new IllegalArgumentException("Route and date must not be null");
        }

        // Finn ukedagen for datoen
        Weekday weekday = Weekday.fromLocalDate(date);

        // Hent alle frekvenser og unntak for ruten og datoen (kombiner begge spørringene)
        List<Frequency> frequencies = frequencyRepositoryMYSQLAdapter.findByRouteAndWeekday(route, weekday);
        List<ExceptionEntry> exceptions = exceptionEntryRepositoryMYSQLAdapter.findByRouteAndDate(route, date);

        // Håndter ukedagsbaserte unntak separat
        List<ExceptionEntry> weekdayExceptions = exceptionEntryRepositoryMYSQLAdapter.findByRouteAndWeekday(route, weekday);
        exceptions.addAll(weekdayExceptions);

        // Fjern eventuelle duplikater og sorter unntakene
        Set<ExceptionEntry> allExceptions = new HashSet<>(exceptions);

        logger.info("Fetched schedule for route {} on date {}", route.getName(), date);

        // Lag og returner den fullstendige tidsplanen
        return new Schedule(route, date, frequencies, new ArrayList<>(allExceptions));
    }

    /**
     * Oppdaterer tidsplanen med nye frekvenser og unntak for en gitt rute og dato.
     */
    public Schedule updateSchedule(Route route, LocalDate date, List<Frequency> newFrequencies, List<ExceptionEntry> newExceptions) {
        if (route == null || date == null) {
            throw new IllegalArgumentException("Route and date must not be null");
        }

        // Hent eksisterende frekvenser og unntak
        List<Frequency> existingFrequencies = frequencyRepositoryMYSQLAdapter.findByRouteAndWeekday(route, Weekday.fromLocalDate(date));
        List<ExceptionEntry> existingExceptions = exceptionEntryRepositoryMYSQLAdapter.findByRouteAndWeekday(route, Weekday.fromLocalDate(date));

        // Oppdater frekvenser
        if (newFrequencies != null) {
            updateFrequencies(existingFrequencies, newFrequencies);
        }

        // Oppdater unntak
        if (newExceptions != null) {
            updateExceptions(existingExceptions, newExceptions);
        }

        // Lag og returner oppdatert tidsplan
        return new Schedule(route, date, newFrequencies != null ? newFrequencies : existingFrequencies,
                newExceptions != null ? newExceptions : existingExceptions);
    }

    private void updateFrequencies(List<Frequency> existingFrequencies, List<Frequency> newFrequencies) {
        // Fjern gamle frekvenser som ikke er i de nye
        for (Frequency existingFrequency : existingFrequencies) {
            if (!newFrequencies.contains(existingFrequency)) {
                frequencyRepositoryMYSQLAdapter.delete(existingFrequency);
                logger.info("Deleted frequency with ID {}", existingFrequency.getId());
            }
        }

        // Legg til nye frekvenser
        for (Frequency newFrequency : newFrequencies) {
            if (!existingFrequencies.contains(newFrequency)) {
                frequencyRepositoryMYSQLAdapter.save(newFrequency);
                logger.info("Saved new frequency with ID {}", newFrequency.getId());
            }
        }
    }

    private void updateExceptions(List<ExceptionEntry> existingExceptions, List<ExceptionEntry> newExceptions) {
        // Fjern gamle unntak som ikke er i de nye
        for (ExceptionEntry existingException : existingExceptions) {
            if (!newExceptions.contains(existingException)) {
                exceptionEntryRepositoryMYSQLAdapter.deleteById(existingException.getId());
                logger.info("Deleted exception with ID {}", existingException.getId());
            }
        }

        // Legg til nye unntak
        for (ExceptionEntry newException : newExceptions) {
            if (!existingExceptions.contains(newException)) {
                exceptionEntryRepositoryMYSQLAdapter.save(newException);
                logger.info("Saved new exception with ID {}", newException.getId());
            }
        }
    }
}