package domain.service;

import domain.model.*;
import domain.repository.*;

import java.time.LocalDate;
import java.util.List;

public class ScheduleService {

    private final RouteRepository routeRepository;
    private final FrequencyRepository frequencyRepository;
    private final ExceptionEntryRepository exceptionRepository;

    public ScheduleService(RouteRepository routeRepository,
                           FrequencyRepository frequencyRepository,
                           ExceptionEntryRepository exceptionRepository) {
        this.routeRepository = routeRepository;
        this.frequencyRepository = frequencyRepository;
        this.exceptionRepository = exceptionRepository;
    }

    /**
     * Bygger en Schedule for en gitt rute og dato.
     * Henter data fra repositories og kombinerer dem.
     *
     * @param routeId ID for ruten
     * @param date datoen for tidsplanen
     * @return en ferdig Schedule
     */
    public Schedule buildSchedule(int routeId, LocalDate date) {
        Route route = routeRepository.read(routeId);
        if (route == null) {
            throw new IllegalArgumentException("Route not found for ID: " + routeId);
        }

        List<Frequency> frequencies = frequencyRepository.readAll().stream()
                .filter(f -> f.getRoute().getId() == routeId)
                .toList();

        List<ExceptionEntry> exceptions = exceptionRepository.readAll().stream()
                .filter(e -> e.getRoute().getId() == routeId && e.appliesTo(date))
                .toList();

        return new Schedule(route, date, frequencies, exceptions);
    }

    public Schedule updateSchedule(int routeId, LocalDate date,
                                   List<Frequency> updatedFrequencies,
                                   List<ExceptionEntry> updatedExceptions) {
        // Oppdater frekvenser
        for (Frequency freq : updatedFrequencies) {
            frequencyRepository.update(freq);
        }

        // Oppdater unntak
        for (ExceptionEntry exception : updatedExceptions) {
            exceptionRepository.update(exception);
        }

        // Bygg ny Schedule med oppdatert data
        return buildSchedule(routeId, date);
    }
}