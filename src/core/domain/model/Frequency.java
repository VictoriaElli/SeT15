package domain.model;

import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


// representerer frekvensen av avganger for en bestemt rute, på en gitt ukedag og i en bestemt sesong.
public class Frequency {
    private int id;
    private Route route;
    private Weekday weekday;
    private Season season;
    private LocalTime firstDeparture;
    private LocalTime lastDeparture;
    private int intervalMinutes;

    // Constructors
    // brukes for frekvenser med ID som er i systemet, og sjekker at intervallet er positivt
    public Frequency(int id, Route route, Weekday weekday, Season season, LocalTime firstDeparture, LocalTime lastDeparture, int intervalMinutes) {
        this.id = id;
        this.route = route;
        this.weekday = weekday;
        this.season = season;
        this.firstDeparture = firstDeparture;
        this.lastDeparture = lastDeparture;
        setIntervalMinutes(intervalMinutes);
    }

    // Methods
    // genererer alle avgangstidspunkter i frekvensen
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

    // sjekker om et tidspunkt faller innefor avgangsperioden
    public boolean isWithinTimeRange(LocalTime time) {
        if (firstDeparture == null || lastDeparture == null) return false;
        return !time.isBefore(firstDeparture) && !time.isAfter(lastDeparture);
    }

    // oppdager overlappende frekvenser for samme rute/sesong/dag
    public boolean conflictsWith(Frequency other) {
        if (!route.equals(other.route)) return false;
        if (!weekday.equals(other.weekday)) return false;
        if (!season.equals(other.season)) return false;

        return !(lastDeparture.isBefore(other.firstDeparture) || firstDeparture.isAfter(other.lastDeparture));
    }


    // regner ut hvor lenge ruten kjører den dagen
    public Duration totalOperationTime() {
        return Duration.between(firstDeparture, lastDeparture);
    }



    // Getters
    public int getId() {
        return id;
    }

    public Route getRoute() {
        return route;
    }

    public Weekday getWeekday() {
        return weekday;
    }

    public Season getSeason() {
        return season;
    }

    public LocalTime getFirstDeparture() {
        return firstDeparture;
    }

    public LocalTime getLastDeparture() {
        return lastDeparture;
    }

    public int getIntervalMinutes() {
        return intervalMinutes;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setWeekday(Weekday weekday) {
        this.weekday = weekday;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public void setFirstDeparture(LocalTime firstDeparture) {
        this.firstDeparture = firstDeparture;
    }

    public void setLastDeparture(LocalTime lastDeparture) {
        this.lastDeparture = lastDeparture;
    }

    // setter avgangsintervall i minutter. må være positivt (> 0).
    public void setIntervalMinutes(int intervalMinutes) {
        if (intervalMinutes <= 0) {
            throw new IllegalArgumentException("Interval minutes must be positive");
        }
        this.intervalMinutes = intervalMinutes;
    }

    // returnerer en lesbar beskrivelse av frekvensen.
    @Override
    public String toString() {
        return String.format("Frequency[route=%s, %s - %s, interval=%d min, weekday=%s]",
                route.getName(), firstDeparture, lastDeparture, intervalMinutes, weekday);
    }
}
