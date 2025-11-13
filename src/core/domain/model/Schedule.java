package domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representerer alle avganger for en rute på en gitt dato.
 *
 * Inneholder informasjon om:
 * - Ruten (Route)
 * - Datoen for tidsplanen (LocalDate)
 * - Frekvenser som bestemmer avgangstidene (Frequency)
 * - Eventuelle unntak (ExceptionEntry) som kan påvirke avganger
 *
 * Funksjonalitet inkluderer:
 * - Hente avganger for ruten på en spesifikk dato
 * - Håndtere både vanlige avganger og spesifikke unntak (som forsinkelser, ekstra avganger, kanselleringer)
 */
public class Schedule {
    // --- Felt ---
    private final Route route;                    // ruten som denne tidsplanen gjelder for
    private final LocalDate date;                  // datoen for denne tidsplanen
    private final List<Frequency> frequencies;     // liste over frekvenser som bestemmer avgangstidene
    private final List<ExceptionEntry> exceptions; // liste over unntak som kan påvirke avganger

    // --- Konstruktører ---
    /**
     * Konstruktør som oppretter en tidsplan for en gitt rute og dato,
     * og setter opp lister for frekvenser og unntak.
     *
     * @param route ruten som tidsplanen gjelder for
     * @param date datoen for denne tidsplanen
     * @param frequencies liste over frekvenser for avganger
     * @param exceptions liste over unntak som kan påvirke avganger
     */
    public Schedule(Route route, LocalDate date, List<Frequency> frequencies, List<ExceptionEntry> exceptions) {
        // Validerer at rute og dato ikke er null
        if (route == null) {
            throw new IllegalArgumentException("Route cannot be null");
        }
        if (date == null) {
            throw new IllegalArgumentException("Date cannot be null");
        }

        // Initialiserer feltene med de angitte verdiene, og tomme lister hvis null
        this.route = route;
        this.date = date;
        this.frequencies = frequencies != null ? frequencies : new ArrayList<>();
        this.exceptions = exceptions != null ? exceptions : new ArrayList<>();
    }

    // --- Metoder ---
    /**
     * Henter alle avganger for ruten på den spesifiserte datoen.
     * Denne metoden returnerer avganger uavhengig av stopp.
     *
     * @return liste med alle avganger (LocalTime)
     */
    public List<LocalTime> getDepartures() {
        return getDeparturesForStop(null); // Henter alle avganger uavhengig av stopp
    }

    /**
     * Henter avganger for et spesifikt stopp på ruten på den spesifiserte datoen.
     * Hvis stopp er null, hentes alle avganger for ruten.
     *
     * @param stop stoppet for hvilke avganger skal hentes
     * @return liste med avganger for det spesifikke stoppet (LocalTime)
     */
    public List<LocalTime> getDeparturesForStop(Stop stop) {
        List<LocalTime> departures = new ArrayList<>(); // Liste for å lagre avganger

        // Henter avganger basert på frekvenser for ruten, datoen og sesongen
        for (Frequency freq : frequencies) {
            // Sjekker om frekvensen gjelder for ruten, dagen og sesongen
            if (freq.getRoute().equals(route)
                    && freq.getWeekday() == Weekday.fromLocalDate(date)
                    && freq.getSeason().isActiveOn(date)) {

                // Henter grunnleggende avgangstider for denne frekvensen
                List<LocalTime> baseDepartures = freq.getDepartureTimes();

                // Hvis et stopp er gitt, justerer vi avgangstiden med reisetiden til stoppet
                if (stop != null) {
                    // Henter stoppet på ruten og justerer avgangstidene
                    RouteStop rs = route.getStops().stream()
                            .filter(s -> s.getStop().equals(stop))
                            .findFirst()
                            .orElse(null);

                    // Hvis stoppet finnes på ruten, justeres avgangstidene
                    if (rs != null) {
                        int minutes = rs.getTimeFromStart(); // Henter reisetid til stoppet
                        baseDepartures = baseDepartures.stream()
                                .map(t -> t.plusMinutes(minutes)) // Legger til reisetid til hver avgang
                                .collect(Collectors.toList());
                    } else {
                        // Hvis stoppet ikke finnes på ruta, hoppes denne frekvensen over
                        continue;
                    }
                }

                // Legger til de grunnleggende avgangstidene for denne frekvensen
                departures.addAll(baseDepartures);
            }
        }

        // --- Håndtering av unntak ---
        // Håndterer eventuelle unntak for forsinkelser, ekstra avganger eller kanselleringer
        for (ExceptionEntry entry : exceptions) {
            // Sjekker om unntaket er aktivt, gjelder for den rette ruten og datoen
            if (!entry.isActive()) continue;
            if (!entry.getRoute().equals(route)) continue;
            if (!entry.appliesTo(date)) continue;

            // Filtrer: gjelder unntaket stoppet, eller er det et generelt unntak?
            if (stop == null || entry.getStop() == null || entry.affectsStop(stop)) {
                LocalTime depTime = entry.getDepartureTime();

                // Håndterer de forskjellige typene unntak
                if (entry.isCancelled() || entry.isOmitted()) {
                    departures.remove(depTime); // Fjerner kansellerte eller utelatte avganger
                } else if (entry.isExtra()) {
                    departures.add(depTime); // Legger til ekstra avganger
                } else if (entry.isDelayed()) {
                    departures.remove(depTime); // Fjerner opprinnelige avganger og legger til forsinkede
                    departures.add(depTime.plusMinutes(5)); // Eksempel på forsinkelse på 5 minutter
                }
            }
        }

        // Fjern duplikater og sorter avganger i stigende rekkefølge
        return departures.stream()
                .distinct() // Fjerner duplikater
                .sorted(Comparator.naturalOrder()) // Sorterer avganger
                .collect(Collectors.toList()); // Samler resultatet tilbake i en liste
    }

    // --- Gettere ---
    /**
     * Henter ruten som denne tidsplanen gjelder for.
     *
     * @return ruten (Route)
     */
    public Route getRoute() {
        return route;
    }

    /**
     * Henter datoen for tidsplanen.
     *
     * @return datoen (LocalDate)
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * Henter alle frekvenser for ruten på denne datoen.
     *
     * @return liste over frekvenser (Frequency)
     */
    public List<Frequency> getFrequencies() {
        return frequencies;
    }

    /**
     * Henter alle unntak som kan påvirke avganger på denne datoen.
     *
     * @return liste over unntak (ExceptionEntry)
     */
    public List<ExceptionEntry> getExceptions() {
        return exceptions;
    }
}
