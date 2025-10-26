package org.byferge.core.domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

// representerer et unntak fra den vanlige rutefrekvensen.
// et unntak kan være knyttet til en spesifikk dato eller til en ukedag i en gitt sesong.
public class ExceptionEntry {
    private int id;
    private Route route;
    private Stop stop;
    private LocalDate validDate;
    private Weekday weekday;
    private Season season;
    private LocalTime departureTime;
    private final ExceptionType type;
    private boolean isActive;
    private final OperationMessage operationMessage; // valgfritt

    // Constructors
    // brukes for unntak som gjelder én spesifikk dato
    public ExceptionEntry(int id, Route route, Stop stop, LocalDate validDate, Season season,
                          LocalTime departureTime, ExceptionType type, boolean isActive,
                          OperationMessage operationMessage) {
        this.id = id;
        this.route = route;
        this.stop = stop;
        this.validDate = validDate;
        this.weekday = null;
        this.season = season;
        this.departureTime = departureTime;
        this.type = type;
        this.isActive = isActive;
        this.operationMessage = operationMessage;
        validateDayDate();
    }

    // brukes for unntak som gjelder én ukedag i en sesong
    public ExceptionEntry(int id, Route route, Stop stop, Weekday weekday, Season season,
                          LocalTime departureTime, ExceptionType type, boolean isActive,
                          OperationMessage operationMessage) {
        this.id = id;
        this.route = route;
        this.stop = stop;
        this.validDate = null;
        this.weekday = weekday;
        this.season = season;
        this.departureTime = departureTime;
        this.type = type;
        this.isActive = isActive;
        this.operationMessage = operationMessage;
        validateDayDate();
    }

    // brukes for nytt dato-basert unntak uten ID.
    public ExceptionEntry(Route route, Stop stop, LocalDate validDate, Season season,
                          LocalTime departureTime, ExceptionType type,
                          OperationMessage operationMessage) {
        this(0, route, stop, validDate, season, departureTime, type, true, operationMessage);
    }

    // brukes for nytt ukedag-basert unntak uten ID.
    public ExceptionEntry(Route route, Stop stop, Weekday weekday, Season season,
                          LocalTime departureTime, ExceptionType type,
                          OperationMessage operationMessage) {
        this(0, route, stop, weekday, season, departureTime, type, true, operationMessage);
    }

    // Methods
    // validering av dag/dato
    public void validateDayDate() {
        if (validDate != null && weekday != null) {
            throw new IllegalStateException("Cannot have both validDate and weekday");
        }
        if (validDate == null && weekday == null) {
            throw new IllegalStateException("Must have either validDate or weekday");
        }
        if (departureTime == null) {
            throw new IllegalStateException("Departure time must be set");
        }
    }


    // sjekker om unntaket gjelder en gitt dato
    public boolean appliesTo(LocalDate date) {
        if (!isActive) return false;

        if (validDate != null) {
            return validDate.equals(date);
        }

        if (weekday != null && season != null && season.isActiveOn(date)) {
            return weekday == Weekday.fromLocalDate(date);
        }

        return false;
    }

    // sjekker om unntaket gjelder et spesifikt stopp
    public boolean affectsStop(Stop checkStop) {
        return stop != null && stop.equals(checkStop);
    }

    // enklere sjekk for kanselleringer
    public boolean isCancelled() {
        return type == ExceptionType.CANCELLED;
    }


    // Getters
    public int getId() {
        return id;
    }

    public Route getRoute() {
        return route;
    }

    public Stop getStop() {
        return stop;
    }

    public LocalDate getValidDate() {
        return validDate;
    }

    public Weekday getWeekday() {
        return weekday;
    }

    public Season getSeason() {
        return season;
    }

    public LocalTime getDepartureTime() {
        return departureTime;
    }

    public ExceptionType getType() {
        return type;
    }

    public boolean isActive() {
        return isActive;
    }

    public OperationMessage getOperationMessage() {
        return operationMessage;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public void setStop(Stop stop) {
        this.stop = stop;
    }

    // setter en dato for unntaket. kan ikke kombineres med ukedag.
    public void setValidDate(LocalDate validDate) {
        if (validDate != null && this.weekday != null) {
            throw new IllegalStateException("Cannot set validDate when weekday is already set");
        }
        this.validDate = validDate;
    }

    // setter en ukedag for unntaket. kan ikke kombineres med dato.
    public void setWeekday(Weekday weekday) {
        if (weekday != null && this.validDate != null) {
            throw new IllegalStateException("Cannot set weekday when validDate is already set");
        }
        this.weekday = weekday;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public void setDepartureTime(LocalTime departureTime) {
        this.departureTime = departureTime;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Override
    @Override
    public String toString() {
        String dateInfo = validDate != null
                ? "Date: " + validDate
                : "Weekday: " + weekday;

        return String.format(
                "ExceptionEntry[id=%d, route=%s, stop=%s, %s, time=%s, type=%s, season=%s, active=%b, msg=%s]",
                id,
                route != null ? route.getRouteNum() : "null",
                stop != null ? stop.getName() : "null",
                dateInfo,
                departureTime != null ? departureTime.toString() : "null",
                type,
                season != null ? season.getSeasonType() + " " + season.getYear() : "null",
                isActive,
                operationMessage != null ? "\"" + operationMessage.getMessage() + "\"" : "null"
        );
    }

}
