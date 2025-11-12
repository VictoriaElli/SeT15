package domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

// Representerer alle avganger for en rute på en gitt dato
public class Schedule {

    private final Route route;
    private final LocalDate date;
    private final List<Frequency> frequencies;
    private final List<ExceptionEntry> exceptions;

    public Schedule(Route route, LocalDate date, List<Frequency> frequencies, List<ExceptionEntry> exceptions) {
        if (route == null) {
            throw new IllegalArgumentException("Route cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        this.route = route;
        this.date = date;
        this.frequencies = frequencies != null ? frequencies : new ArrayList<>();
        this.exceptions = exceptions != null ? exceptions : new ArrayList<>();
    }

    // Henter alle avganger for ruta
    public List<LocalTime> getDepartures() {
        return getDeparturesForStop(null); // henter alle avganger uavhengig av stopp
    }

    // Henter avganger for et spesifikt stopp
    public List<LocalTime> getDeparturesForStop(Stop stop) {
        List<LocalTime> departures = new ArrayList<>();

        // Hent frekvens-baserte avganger
        for (Frequency freq : frequencies) {
            if (freq.getRoute().equals(route)
                    && freq.getWeekday() == Weekday.fromLocalDate(date)
                    && freq.getSeason().isActiveOn(date)) {

                // Grunnavganger for denne frekvensen
                List<LocalTime> baseDepartures = freq.getDepartureTimes();

                // Hvis et spesifikt stopp er gitt, legg til reisetid til stoppet
                if (stop != null) {
                    RouteStop rs = route.getStops().stream()
                            .filter(s -> s.getStop().equals(stop))
                            .findFirst()
                            .orElse(null);

                    if (rs != null) {
                        int minutes = rs.getTimeFromStart();
                        baseDepartures = baseDepartures.stream()
                                .map(t -> t.plusMinutes(minutes))
                                .collect(Collectors.toList());
                    } else {
                        // Hvis stoppet ikke finnes på ruta, hopp over denne frekvensen
                        continue;
                    }
                }

                departures.addAll(baseDepartures);
            }
        }

        // Håndter unntak
        for (ExceptionEntry entry : exceptions) {
            if (!entry.isActive()) continue;
            if (!entry.getRoute().equals(route)) continue;
            if (!entry.appliesTo(date)) continue;

            // Filtrer: gjelder stoppet, eller er generelt (null)
            if (stop == null || entry.getStop() == null || entry.affectsStop(stop)) {
                LocalTime depTime = entry.getDepartureTime();

                if (entry.isCancelled() || entry.isOmitted()) {
                    departures.remove(depTime);
                } else if (entry.isExtra()) {
                    departures.add(depTime);
                } else if (entry.isDelayed()) {
                    departures.remove(depTime);
                    departures.add(depTime.plusMinutes(5)); // eksempel-forsinkelse
                }
            }
        }

        // Fjern duplikater og sorter
        return departures.stream()
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    // Getters
    public Route getRoute() {
        return route;
    }

    public LocalDate getDate() {
        return date;
    }

    public List<Frequency> getFrequencies() {
        return frequencies;
    }

    public List<ExceptionEntry> getExceptions() {
        return exceptions;
    }
}
