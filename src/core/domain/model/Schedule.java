package domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// Representerer alle avganger for en rute på en gitt dato
public class Schedule {

    private final Route route;
    private final LocalDate date;
    private final List<Frequency> frequencies;
    private final List<ExceptionEntry> exceptions;

    public Schedule(Route route, LocalDate date, List<Frequency> frequencies, List<ExceptionEntry> exceptions) {
        this.route = route;
        this.date = date;
        this.frequencies = frequencies;
        this.exceptions = exceptions;
    }

    // Genererer alle avgangstidene for datoen
    public List<LocalTime> getDepartures() {
        List<LocalTime> departures = new ArrayList<>();

        // Legg til alle frekvensbaserte avganger som gjelder på denne dagen
        for (Frequency freq : frequencies) {
            if (freq.getWeekday() == Weekday.fromLocalDate(date) && freq.getSeason().isActiveOn(date)) {
                departures.addAll(freq.getDepartureTimes());
            }
        }

        // Behandle ExceptionEntries
        for (ExceptionEntry entry : exceptions) {
            if (entry.appliesTo(date)) {
                if (entry.isCancelled() || entry.isOmitted()) {
                    departures.remove(entry.getDepartureTime()); // fjern kansellerte/utelatte avganger
                } else if (entry.isExtra()) {
                    departures.add(entry.getDepartureTime()); // legg til ekstra
                } else if (entry.isDelayed()) {
                    departures.remove(entry.getDepartureTime()); // fjern gammel tid
                    departures.add(entry.getDepartureTime().plusMinutes(5)); // eksempel: forsink 5 min
                }
            }
        }

        // Sorter tidene
        return departures.stream().sorted().collect(Collectors.toList());
    }

}
