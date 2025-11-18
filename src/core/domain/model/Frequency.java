package domain.model;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Representerer frekvensen av avganger for en gitt rute, på en bestemt ukedag og i en gitt sesong.
 * Eks: Rute 101, mandag, sommer, første avgang 06:00, siste 22:00, intervall 30 min.
 */
@Entity
public class Frequency {

    // --- Felt ---

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;                     // Unik ID generert av databasen

    @ManyToOne
    @JoinColumn(name = "routeId")
    private Route route;                // Ruten frekvensen gjelder

    @Enumerated(EnumType.STRING)
    @Column(name = "weekday")
    private Weekday weekday;            // Ukedag frekvensen gjelder

    @ManyToOne
    @JoinColumn(name = "seasonId")
    private Season season;              // Sesong frekvensen gjelder

    @Column(name = "firstDeparture")
    private LocalTime firstDeparture;   // Første avgangstid

    @Column(name = "lastDeparture")
    private LocalTime lastDeparture;    // Siste avgangstid

    @Column(name = "intervalMinutes")
    private int intervalMinutes;        // Minutter mellom avganger (> 0)


    // --- Konstruktør ---

    public Frequency(int id, Route route, Weekday weekday, Season season,
                     LocalTime firstDeparture, LocalTime lastDeparture, int intervalMinutes) {

        this.id = id;
        this.route = route;
        this.weekday = weekday;
        this.season = season;
        this.firstDeparture = firstDeparture;
        this.lastDeparture = lastDeparture;
        setIntervalMinutes(intervalMinutes);
    }


    // --- Metoder ---

    /**
     * Genererer alle avgangstidspunkter fra første til siste avgang med gitt intervall.
     * Returnerer tom liste hvis noen felt mangler eller intervallet er <= 0.
     */
    public List<LocalTime> getDepartureTimes() {
        List<LocalTime> times = new ArrayList<>();
        if (firstDeparture == null || lastDeparture == null || intervalMinutes <= 0)
            return times;

        LocalTime t = firstDeparture;

        // Hvis siste avgang er neste dag (over midnatt)
        boolean wrapsToNextDay = lastDeparture.isBefore(firstDeparture);

        int safety = 0;  // hindrer infinite loops

        while (true) {

            times.add(t);

            // Hvis normal dag
            if (!wrapsToNextDay && t.equals(lastDeparture))
                break;

            // Hvis over midnatt
            if (wrapsToNextDay && t.equals(lastDeparture))
                break;

            t = t.plusMinutes(intervalMinutes);

            // Sikring: stopp hvis det går mer enn 24h med intervaller
            safety++;
            if (safety > (24 * 60) / intervalMinutes + 2)
                break; // unngår infinite loop
        }

        return times;
    }


    /**
     * Sjekker om et tidspunkt ligger innenfor første og siste avgang.
     */
    public boolean isWithinTimeRange(LocalTime time) {
        if (firstDeparture == null || lastDeparture == null) return false;
        return !time.isBefore(firstDeparture) && !time.isAfter(lastDeparture);
    }

    /**
     * Sjekker om to frekvenser overlapper for samme rute, ukedag og sesong.
     */
    public boolean conflictsWith(Frequency other) {
        if (!route.equals(other.route)) return false;
        if (!weekday.equals(other.weekday)) return false;
        if (!season.equals(other.season)) return false;

        return !(lastDeparture.isBefore(other.firstDeparture)
                || firstDeparture.isAfter(other.lastDeparture));
    }

    /**
     * Total driftstid for perioden.
     */
    public Duration totalOperationTime() {
        return Duration.between(firstDeparture, lastDeparture);
    }

    /**
     * Returnerer alle avgangstidspunkter med delay lagt til (uten å endre original).
     */
    public List<LocalTime> getDelayedDepartureTimes(int delayMinutes) {
        return getDepartureTimes().stream()
                .map(t -> t.plusMinutes(delayMinutes))
                .collect(Collectors.toList());
    }

    /**
     * Sjekk om en Frequency matcher en ExceptionEntry (kan være basert på rute og tidspunkt).
     */
    public boolean matches(ExceptionEntry entry) {
        if (!route.equals(entry.getRoute())) return false;

        // Hvis entry har konkret dato eller ukedag, sjekk om noen avgangstid faller innenfor
        LocalTime entryTime = entry.getDepartureTime(); // du må ha et tidspunkt på ExceptionEntry
        return entryTime != null && isWithinTimeRange(entryTime);
    }

    /**
     * Legg til en ekstra avgang basert på en ExceptionEntry.
     * Returnerer nytt LocalTime som skal legges inn i schedule.
     */
    public LocalTime toFrequency(ExceptionEntry extraEntry) {
        return extraEntry.getDepartureTime(); // du kan justere med delay her også hvis ønskelig
    }

    // --- Gettere / Settere ---

    public int getId() { return id; }
    public Route getRoute() { return route; }
    public Weekday getWeekday() { return weekday; }
    public Season getSeason() { return season; }
    public LocalTime getFirstDeparture() { return firstDeparture; }
    public LocalTime getLastDeparture() { return lastDeparture; }
    public int getIntervalMinutes() { return intervalMinutes; }

    public void setId(int id) { this.id = id; }
    public void setRoute(Route route) { this.route = route; }
    public void setWeekday(Weekday weekday) { this.weekday = weekday; }
    public void setSeason(Season season) { this.season = season; }
    public void setFirstDeparture(LocalTime firstDeparture) { this.firstDeparture = firstDeparture; }
    public void setLastDeparture(LocalTime lastDeparture) { this.lastDeparture = lastDeparture; }

    /**
     * Setter intervall mellom avganger. Må være > 0.
     */
    public void setIntervalMinutes(int intervalMinutes) {
        if (intervalMinutes <= 0) throw new IllegalArgumentException("Interval minutes must be positive");
        this.intervalMinutes = intervalMinutes;
    }

    // --- Overrides ---

    @Override
    public String toString() {
        return String.format("Frequency[route=%s, %s-%s, interval=%d, weekday=%s]",
                route.getName(),
                firstDeparture,
                lastDeparture,
                intervalMinutes,
                weekday);
    }
}
