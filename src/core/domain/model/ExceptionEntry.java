package domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Representerer et unntak fra vanlig rutefrekvens.
 *
 * Et unntak kan gjelde:
 * - en spesifikk dato (validDate)
 * - en ukedag innenfor en sesong (weekday + season)
 *
 * Unntak kan ha type EXTRA, DELAYED, CANCELLED, eller OMITTED, og kan være aktivt eller ikke.
 * Det kan også legges til en valgfri melding til passasjerer (OperationMessage).
 */
@Entity
public class ExceptionEntry {

    // --- Felt ---

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;                       // Unik ID generert av databasen

    @ManyToOne
    @JoinColumn(name = "routeId")
    private Route route;                  // Ruten unntaket gjelder

    @ManyToOne
    @JoinColumn(name = "stopId")
    private Stops stop;                    // Stoppet unntaket gjelder (null = gjelder alle stopp)

    @Column(name = "validDate")
    private LocalDate validDate;          // Spesifikk dato for unntaket

    @Enumerated(EnumType.STRING)
    @Column(name = "weekday")
    private Weekday weekday;              // Ukedag for unntaket (alternativ til validDate)

    @ManyToOne
    @JoinColumn(name = "seasonId")
    private Season season;                // Sesong unntaket gjelder (brukes med weekday)

    @Column(name = "departureTime")
    private LocalTime departureTime;      // Tidspunkt for unntaket

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private ExceptionType type;           // Type unntak: EXTRA, DELAYED, CANCELLED, OMITTED

    @Column(name = "isActive")
    private boolean isActive = true;      // Om unntaket er aktivt (standard: true)

    @ManyToOne
    @JoinColumn(name = "operationMessageId")
    private OperationMessage operationMessage;  // Valgfri melding til passasjerer

    // --- Builder-pattern for enkel oppretting ---

    public static class Builder {
        private int id;
        private Route route;
        private Stops stop = null;
        private LocalDate validDate;
        private Weekday weekday;
        private Season season;
        private LocalTime departureTime;
        private ExceptionType type;
        private boolean isActive = true;
        private OperationMessage operationMessage = null;

        public Builder setId(int id) { this.id = id; return this; }
        public Builder setRoute(Route route) { this.route = route; return this; }
        public Builder setStop(Stops stop) { this.stop = stop; return this; }
        public Builder setValidDate(LocalDate validDate) { this.validDate = validDate; return this; }
        public Builder setWeekday(Weekday weekday) { this.weekday = weekday; return this; }
        public Builder setSeason(Season season) { this.season = season; return this; }
        public Builder setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; return this; }
        public Builder setType(ExceptionType type) { this.type = type; return this; }
        public Builder setIsActive(boolean isActive) { this.isActive = isActive; return this; }
        public Builder setOperationMessage(OperationMessage operationMessage) { this.operationMessage = operationMessage; return this; }

        /**
         * Bygger ExceptionEntry og validerer at nødvendige felt er satt.
         */
        public ExceptionEntry build() {
            if (validDate == null && weekday == null) {
                throw new IllegalStateException("Either validDate or weekday must be set");
            }
            if (departureTime == null) {
                throw new IllegalStateException("Departure time must be set");
            }
            return new ExceptionEntry(id, route, stop, validDate, weekday, season, departureTime, type, isActive, operationMessage);
        }
    }

    // --- Privat konstruktør, kun for builder ---
    private ExceptionEntry(int id, Route route, Stops stop, LocalDate validDate, Weekday weekday,
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

    /**
     * Tom konstruktør nødvendig for JPA/Hibernate.
     */
    protected ExceptionEntry() {}

    // --- Validering ---

    /**
     * Validerer at unntaket har enten validDate eller weekday (men ikke begge), og at departureTime er satt.
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

    // --- Sjekk om unntaket gjelder en dato eller stopp ---

    /**
     * Sjekker om unntaket gjelder for en gitt dato.
     * Returnerer false hvis unntaket ikke er aktivt.
     */
    public boolean appliesTo(LocalDate date) {
        if (!isActive) return false;
        if (validDate != null && validDate.equals(date)) return true;
        if (weekday != null && season != null && season.isActiveOn(date)) {
            return weekday == Weekday.fromLocalDate(date);
        }
        return false;
    }

    /**
     * Sjekker om unntaket gjelder et spesifikt stopp.
     * Returnerer true hvis stop er null (gjelder alle stopp) eller matcher checkStop.
     */
    public boolean affectsStop(Stops checkStop) {
        return stop == null || stop.equals(checkStop);
    }

    // --- Enkle type-sjekk metoder ---
    public boolean isExtra() { return type == ExceptionType.EXTRA; }
    public boolean isDelayed() { return type == ExceptionType.DELAYED; }
    public boolean isCancelled() { return type == ExceptionType.CANCELLED; }
    public boolean isOmitted() { return type == ExceptionType.OMITTED; }

    // --- Gettere ---
    public int getId() { return id; }
    public Route getRoute() { return route; }
    public Stops getStop() { return stop; }
    public LocalDate getValidDate() { return validDate; }
    public Weekday getWeekday() { return weekday; }
    public Season getSeason() { return season; }
    public LocalTime getDepartureTime() { return departureTime; }
    public ExceptionType getType() { return type; }
    public boolean isActive() { return isActive; }
    public OperationMessage getOperationMessage() { return operationMessage; }

    // --- Settere ---
    public void setId(int id) { this.id = id; }
    public void setRoute(Route route) { this.route = route; }
    public void setStop(Stops stop) { this.stop = stop; }

    /**
     * Setter validDate. Kan ikke kombineres med ukedag.
     */
    public void setValidDate(LocalDate validDate) {
        if (validDate != null && this.weekday != null) {
            throw new IllegalStateException("Cannot set validDate when weekday is already set");
        }
        this.validDate = validDate;
    }

    /**
     * Setter ukedag. Kan ikke kombineres med dato.
     */
    public void setWeekday(Weekday weekday) {
        if (weekday != null && this.validDate != null) {
            throw new IllegalStateException("Cannot set weekday when validDate is already set");
        }
        this.weekday = weekday;
    }

    public void setSeason(Season season) { this.season = season; }
    public void setDepartureTime(LocalTime departureTime) { this.departureTime = departureTime; }
    public void setType(ExceptionType type) { this.type = type; }
    public void setActive(boolean active) { isActive = active; }
    public void setOperationMessage(OperationMessage operationMessage) { this.operationMessage = operationMessage; }

    // --- Overrides ---

    @Override
    public String toString() {
        String dateInfo = validDate != null ? "Date: " + validDate : "Weekday: " + weekday;
        return String.format(
                "ExceptionEntry[id=%d, route=%s, stop=%s, %s, time=%s, type=%s, season=%s, active=%b, msg=%s]",
                id,
                Objects.toString(route, "No route"),
                Objects.toString(stop, "No stop"),
                dateInfo,
                departureTime,
                type,
                Objects.toString(season, "No season"),
                isActive,
                Objects.toString(operationMessage, "No message")
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExceptionEntry that = (ExceptionEntry) o;
        return id == that.id &&
                isActive == that.isActive &&
                Objects.equals(route, that.route) &&
                Objects.equals(stop, that.stop) &&
                Objects.equals(validDate, that.validDate) &&
                weekday == that.weekday &&
                Objects.equals(season, that.season) &&
                Objects.equals(departureTime, that.departureTime) &&
                type == that.type &&
                Objects.equals(operationMessage, that.operationMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, route, stop, validDate, weekday, season, departureTime, type, isActive, operationMessage);
    }
}
