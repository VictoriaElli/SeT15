package domain.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Representerer et unntak fra den vanlige rutefrekvensen.
 * Et unntak kan være knyttet til:
 * - en spesifikk dato (validDate)
 * - en ukedag innenfor en sesong (weekday + season)
 *
 * Unntak kan ha ulike typer (EXTRA, DELAYED, CANCELLED, OMITTED) og kan være aktivt eller ikke.
 * Valgfri melding (OperationMessage) kan legges til for å informere passasjerer.
 */
public class ExceptionEntry {
    // --- Felt ---
    private int id;                       // unik identifikator for unntaket
    private Route route;                  // ruten unntaket gjelder
    private Stop stop;                    // stoppet unntaket gjelder, null betyr alle stopp
    private LocalDate validDate;          // spesifikk dato for unntaket
    private Weekday weekday;              // ukedag for unntaket (alternativ til validDate)
    private Season season;                // sesong unntaket gjelder, brukt sammen med weekday
    private LocalTime departureTime;      // tidspunkt for unntaket
    private final ExceptionType type;     // type unntak (EXTRA, DELAYED, CANCELLED, OMITTED)
    private boolean isActive;             // om unntaket er aktivt
    private OperationMessage operationMessage; // valgfri melding til passasjerer

    public static class Builder {
        private int id;
        private Route route;
        private Stop stop = null;
        private LocalDate validDate;
        private Weekday weekday;
        private Season season;
        private LocalTime departureTime;
        private ExceptionType type;
        private boolean isActive = true;
        private OperationMessage operationMessage = null;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setRoute(Route route) {
            this.route = route;
            return this;
        }

        public Builder setStop(Stop stop) {
            this.stop = stop;
            return this;
        }

        public Builder setValidDate(LocalDate validDate) {
            this.validDate = validDate;
            return this;
        }

        public Builder setWeekday(Weekday weekday) {
            this.weekday = weekday;
            return this;
        }

        public Builder setSeason(Season season) {
            this.season = season;
            return this;
        }

        public Builder setDepartureTime(LocalTime departureTime) {
            this.departureTime = departureTime;
            return this;
        }

        public Builder setType(ExceptionType type) {
            this.type = type;
            return this;
        }

        public Builder setIsActive(boolean isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder setOperationMessage(OperationMessage operationMessage) {
            this.operationMessage = operationMessage;
            return this;
        }

        public ExceptionEntry build() {
            // Validere at vi har satt enten validDate eller weekday
            if (validDate == null && weekday == null) {
                throw new IllegalStateException("Either validDate or weekday must be set");
            }

            // Validere at det finnes et gyldig departureTime
            if (departureTime == null) {
                throw new IllegalStateException("Departure time must be set");
            }

            return new ExceptionEntry(id, route, stop, validDate, weekday, season, departureTime, type, isActive, operationMessage);
        }
    }

    // --- Privat konstruktør ---
    private ExceptionEntry(int id, Route route, Stop stop, LocalDate validDate, Weekday weekday,
                           Season season, LocalTime departureTime, ExceptionType type,
                           boolean isActive, OperationMessage operationMessage) {
        this.id = id;
        this.route = route;
        this.stop = stop;
        this.validDate = validDate;
        this.weekday = weekday;
        this.season = season;
        this.departureTime = departureTime;
        this.type = type;
        this.isActive = isActive;
        this.operationMessage = operationMessage;
    }

    // --- Validering ---
    /**
     * Validerer at unntaket har enten validDate eller weekday (men ikke begge) og at departureTime er satt.
     */
    public void validateDayDate() {
        if (validDate != null && weekday != null) {
            throw new IllegalStateException("Both validDate and weekday are set. Only one should be set.");
        }
        if (validDate == null && weekday == null) {
            throw new IllegalStateException("Either validDate or weekday must be set, but not both.");
        }
        if (departureTime == null) {
            throw new IllegalStateException("Departure time must be set.");
        }
    }

    // --- Metoder for å sjekke om unntaket gjelder en dato eller stopp ---
    /**
     * Sjekker om unntaket gjelder for en gitt dato.
     * Returnerer false hvis unntaket ikke er aktivt.
     */
    public boolean appliesTo(LocalDate date) {
        if (!isActive) return false;

        if (validDate != null && validDate.equals(date)) {
            return true;
        }

        if (weekday != null && season != null && season.isActiveOn(date)) {
            return weekday == Weekday.fromLocalDate(date);
        }

        return false;
    }

    /**
     * Sjekker om unntaket gjelder et spesifikt stopp.
     * Returnerer true hvis stop er null (gjelder alle stopp) eller matcher checkStop.
     */
    public boolean affectsStop(Stop checkStop) {
        return stop == null || stop.equals(checkStop);
    }

    // --- Enklere type-sjekk metoder ---
    public boolean isExtra() { return type == ExceptionType.EXTRA; }
    public boolean isDelayed() { return type == ExceptionType.DELAYED; }
    public boolean isCancelled() { return type == ExceptionType.CANCELLED; }
    public boolean isOmitted() { return type == ExceptionType.OMITTED; }

    // --- Getters ---
    public int getId() { return id; }
    public Route getRoute() { return route; }
    public Stop getStop() { return stop; }
    public LocalDate getValidDate() { return validDate; }
    public Weekday getWeekday() { return weekday; }
    public Season getSeason() { return season; }
    public LocalTime getDepartureTime() { return departureTime; }
    public ExceptionType getType() { return type; }
    public boolean isActive() { return isActive; }
    public OperationMessage getOperationMessage() { return operationMessage; }

    // --- Setters ---
    public void setId(int id) { this.id = id; }
    public void setRoute(Route route) { this.route = route; }
    public void setStop(Stop stop) { this.stop = stop; }

    /** Setter validDate. Kan ikke kombineres med ukedag. */
    public void setValidDate(LocalDate validDate) {
        if (validDate != null && this.weekday != null) {
            throw new IllegalStateException("Cannot set validDate when weekday is already set");
        }
        this.validDate = validDate;
    }

    /** Setter ukedag. Kan ikke kombineres med dato. */
    public void setWeekday(Weekday weekday) {
        if (weekday != null && this.validDate != null) {
            throw new IllegalStateException("Cannot set weekday when validDate is already set");
        }
        this.weekday = weekday;
    }

    public void setSeason(Season season) { this.season = season; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }
    public void setActive(boolean active) { isActive = active; }
    public void setOperationMessage(OperationMessage operationMessage) { this.operationMessage = operationMessage; }

    // --- Overrides ---
    @Override
    public String toString() {
        String dateInfo = validDate != null ? "Date: " + validDate : "Weekday: " + weekday;

        return String.format(
                "ExceptionEntry[id=%d, route=%s, stop=%s, %s, time=%s, type=%s, season=%s, active=%b, msg=%s]",
                id,
                Objects.toString(route, "null"),  // Bruker Objects.toString() for å håndtere null
                Objects.toString(stop, "null"),  // Bruker Objects.toString() for å håndtere null
                dateInfo,
                Objects.toString(departureTime, "null"),  // Bruker Objects.toString() for å håndtere null
                type,
                Objects.toString(season != null ? season.getSeasonType() + " " + season.getYear() : null, "null"),
                isActive,
                Objects.toString(operationMessage != null ? "\"" + operationMessage.getMessage() + "\"" : null, "null")
        );
    }
}
