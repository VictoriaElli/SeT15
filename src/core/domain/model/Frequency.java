package domain.model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representerer frekvensen av avganger for en bestemt rute, på en gitt ukedag og i en bestemt sesong.
 *
 * Frekvensen har:
 * - starttidspunkt (firstDeparture)
 * - sluttidspunkt (lastDeparture)
 * - intervall mellom avganger (intervalMinutes)
 *
 * Den kan generere alle avgangstidspunkter, sjekke om et tidspunkt faller innenfor frekvensen,
 * oppdage overlappende frekvenser og beregne total driftstid for dagen.
 */
public class Frequency {

    // --- Felt ---
    private int id;                   // unik identifikator for frekvensen
    private Route route;              // ruten frekvensen gjelder
    private Weekday weekday;          // ukedagen frekvensen gjelder
    private Season season;            // sesongen frekvensen gjelder
    private LocalTime firstDeparture; // tidspunkt for første avgang
    private LocalTime lastDeparture;  // tidspunkt for siste avgang
    private int intervalMinutes;      // intervall mellom avganger i minutter (må være > 0)

    // --- Konstruktører ---
    /**
     * Fullkonstruktør med ID (for frekvenser som allerede finnes i systemet).
     * Validerer at intervalMinutes er positivt.
     *
     * @param id unik identifikator
     * @param route ruten frekvensen gjelder
     * @param weekday ukedagen
     * @param season sesongen
     * @param firstDeparture tidspunkt for første avgang
     * @param lastDeparture tidspunkt for siste avgang
     * @param intervalMinutes intervall mellom avganger i minutter
     */
    public Frequency(int id, Route route, Weekday weekday, Season season,
                     LocalTime firstDeparture, LocalTime lastDeparture, int intervalMinutes) {
        this.id = id;
        this.route = route;
        this.weekday = weekday;
        this.season = season;
        this.firstDeparture = firstDeparture;
        this.lastDeparture = lastDeparture;
        setIntervalMinutes(intervalMinutes); // validerer at intervallet > 0
    }

    // --- Metoder ---

    /**
     * Genererer en liste over alle avgangstidspunkter for denne frekvensen.
     * Returnerer tom liste hvis start/slutt eller intervall er ugyldig.
     */
    public List<LocalTime> getDepartureTimes() {
        List<LocalTime> times = new ArrayList<>();
        if (firstDeparture == null || lastDeparture == null || intervalMinutes <= 0) return times;

        LocalTime current = firstDeparture;
        while (!current.isAfter(lastDeparture)) {
            times.add(current);
            current = current.plusMinutes(intervalMinutes);
        }

        return times;
    }

    /**
     * Sjekker om et gitt tidspunkt faller innenfor avgangsperioden.
     *
     * @param time tidspunkt å sjekke
     * @return true hvis time >= firstDeparture og time <= lastDeparture
     */
    public boolean isWithinTimeRange(LocalTime time) {
        if (firstDeparture == null || lastDeparture == null) return false;
        return !time.isBefore(firstDeparture) && !time.isAfter(lastDeparture);
    }

    /**
     * Sjekker om denne frekvensen overlapper med en annen frekvens for samme rute, ukedag og sesong.
     *
     * @param other annen frekvens å sammenligne med
     * @return true hvis det er overlapp
     */
    public boolean conflictsWith(Frequency other) {
        if (!route.equals(other.route)) return false;        // ulike ruter kan ikke overlappe
        if (!weekday.equals(other.weekday)) return false;   // ulike ukedager kan ikke overlappe
        if (!season.equals(other.season)) return false;     // ulike sesonger kan ikke overlappe

        // Overlapp hvis starttid/stoppid intervaller krysser hverandre
        return !(lastDeparture.isBefore(other.firstDeparture) || firstDeparture.isAfter(other.lastDeparture));
    }

    /**
     * Beregner total driftstid for frekvensen.
     *
     * @return Duration mellom første og siste avgang
     */
    public Duration totalOperationTime() {
        return Duration.between(firstDeparture, lastDeparture);
    }

    // --- Getters ---
    public int getId() { return id; }
    public Route getRoute() { return route; }
    public Weekday getWeekday() { return weekday; }
    public Season getSeason() { return season; }
    public LocalTime getFirstDeparture() { return firstDeparture; }
    public LocalTime getLastDeparture() { return lastDeparture; }
    public int getIntervalMinutes() { return intervalMinutes; }

    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setRoute(Route route) { this.route = route; }
    public void setWeekday(Weekday weekday) { this.weekday = weekday; }
    public void setSeason(Season season) { this.season = season; }
    public void setFirstDeparture(LocalTime firstDeparture) { this.firstDeparture = firstDeparture; }
    public void setLastDeparture(LocalTime lastDeparture) { this.lastDeparture = lastDeparture; }

    /**
     * Setter intervall i minutter. Må være positivt (> 0).
     *
     * @param intervalMinutes intervall mellom avganger
     */
    public void setIntervalMinutes(int intervalMinutes) {
        if (intervalMinutes <= 0) {
            throw new IllegalArgumentException("Interval minutes must be positive");
        }
        this.intervalMinutes = intervalMinutes;
    }

    /**
     * Returnerer en lesbar beskrivelse av frekvensen.
     */
    @Override
    public String toString() {
        return String.format("Frequency[route=%s, %s - %s, interval=%d min, weekday=%s]",
                route.getName(),
                firstDeparture != null ? firstDeparture : "null",
                lastDeparture != null ? lastDeparture : "null",
                intervalMinutes,
                weekday != null ? weekday : "null");
    }
}
