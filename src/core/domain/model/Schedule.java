package domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representerer alle avganger for en rute på en bestemt dato.
 * Håndterer både faste frekvenser og unntak som kanselleringer eller ekstraavganger.
 */
public class Schedule {

    // --- Felt ---

    private final Route route;                     // Ruten tidsplanen gjelder for
    private final LocalDate date;                  // Dato for tidsplanen
    private final List<Frequency> frequencies;    // Frekvenser som definerer faste avgangstider
    private final List<ExceptionEntry> exceptions;// Unntak som påvirker avganger (f.eks. kanselleringer, forsinkelser)

    // --- Konstruktør ---

    /**
     * Oppretter en ny tidsplan for en rute på en gitt dato.
     * Hvis frekvenser eller unntak er null, initialiseres tomme lister.
     */
    public Schedule(Route route, LocalDate date, List<Frequency> frequencies, List<ExceptionEntry> exceptions) {
        if (route == null) throw new IllegalArgumentException("Route cannot be null");
        if (date == null) throw new IllegalArgumentException("Date cannot be null");

        this.route = route;
        this.date = date;
        this.frequencies = frequencies != null ? frequencies : new ArrayList<>();
        this.exceptions = exceptions != null ? exceptions : new ArrayList<>();
    }

    // --- Metoder ---

    /**
     * Henter alle avgangstider for ruten på datoen til Schedule.
     * Tar hensyn til frekvenser og unntak.
     */
    public List<LocalTime> getDepartures() {
        return getDeparturesForStop(null);
    }

    /**
     * Henter avgangstider for et spesifikt stopp.
     * Beregner tiden fra rutenes starttidspunkt, og tar hensyn til unntak.
     *
     * @param stop Stoppested som avganger skal beregnes for. Null for hele ruten.
     * @return Liste av LocalTime for avgangene, sortert og unike.
     */
    public List<LocalTime> getDeparturesForStop(Stops stop) {
        List<LocalTime> departures = new ArrayList<>();

        // Gå gjennom alle frekvenser og finn de som gjelder denne ruten, dato og sesong
        for (Frequency freq : frequencies) {
            if (freq.getRoute().equals(route) &&
                    freq.getWeekday() == Weekday.fromLocalDate(date) &&
                    freq.getSeason().isActiveOn(date)) {

                List<LocalTime> baseDepartures = freq.getDepartureTimes();

                // Hvis vi skal hente avganger for et spesifikt stopp, legg til tidsforskyvning
                if (stop != null) {
                    RouteStops rs = route.getStops().stream()
                            .filter(s -> s.getStop().equals(stop))
                            .findFirst()
                            .orElse(null);

                    if (rs != null) {
                        int minutes = rs.getTimeFromStart();
                        baseDepartures = baseDepartures.stream()
                                .map(t -> t.plusMinutes(minutes))
                                .collect(Collectors.toList());
                    } else continue; // Stoppet finnes ikke på ruten
                }

                departures.addAll(baseDepartures);
            }
        }

        // Håndter unntak (kansellering, ekstraavganger, forsinkelser)
        List<LocalTime> finalDepartures = new ArrayList<>(departures);
        for (ExceptionEntry entry : exceptions) {
            if (!entry.isActive() || !entry.getRoute().equals(route) || !entry.appliesTo(date)) continue;
            if (stop != null && entry.getStop() != null && !entry.affectsStop(stop)) continue;

            LocalTime depTime = entry.getDepartureTime();
            if (entry.isCancelled() || entry.isOmitted()) finalDepartures.remove(depTime);
            else if (entry.isExtra()) finalDepartures.add(depTime);
            else if (entry.isDelayed()) {
                finalDepartures.remove(depTime);
                finalDepartures.add(depTime.plusMinutes(5));
            }
        }

        // Sorter og fjern duplikater før returnering
        return finalDepartures.stream()
                .distinct()
                .sorted(Comparator.naturalOrder())
                .collect(Collectors.toList());
    }

    // --- Gettere ---

    public Route getRoute() { return route; }                       // Ruten tidsplanen gjelder for
    public LocalDate getDate() { return date; }                     // Dato for tidsplanen
    public List<Frequency> getFrequencies() { return frequencies; } // Liste av frekvenser
    public List<ExceptionEntry> getExceptions() { return exceptions; } // Liste av unntak
}
