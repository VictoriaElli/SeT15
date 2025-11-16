package domain.model;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

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
        if (firstDeparture == null || lastDeparture == null || intervalMinutes <= 0) return times;

        LocalTime t = firstDeparture;
        while (!t.isAfter(lastDeparture)) {
            times.add(t);
            t = t.plusMinutes(intervalMinutes);
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
