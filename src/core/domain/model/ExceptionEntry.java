package domain.model;

import java.time.LocalDate;
import java.time.LocalTime;

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
    private final OperationMessage operationMessage; // valgfri melding til passasjerer

    // --- Fullkonstruktør ---
    /**
     * Fullkonstruktør som initialiserer alle felt.
     * Validerer at enten validDate eller weekday er satt (ikke begge) og at departureTime er satt.
     */
    public ExceptionEntry(int id, Route route, Stop stop,
                          LocalDate validDate, Weekday weekday,
                          Season season, LocalTime departureTime,
                          ExceptionType type, boolean isActive,
                          OperationMessage operationMessage) {
        if (type == null)
            throw new IllegalArgumentException("ExceptionType cannot be null");

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

        validateDayDate(); // sikrer konsistens mellom dato/ukedag og tidspunkt
    }

    // --- Dato-baserte konstruktører ---
    // Disse brukes når unntaket gjelder en spesifikk dato (validDate)
    /** Med stopp og melding */
    public ExceptionEntry(Route route, Stop stop, LocalDate validDate,
                          Season season, LocalTime departureTime,
                          ExceptionType type, OperationMessage operationMessage) {
        // kaller fullkonstruktør med weekday = null, id = 0, isActive = true
        this(0, route, stop, validDate, null, season, departureTime, type, true, operationMessage);
    }

    /** Med stopp, uten melding */
    public ExceptionEntry(Route route, Stop stop, LocalDate validDate,
                          Season season, LocalTime departureTime,
                          ExceptionType type) {
        // samme som over, men operationMessage = null
        this(0, route, stop, validDate, null, season, departureTime, type, true, null);
    }

    /** Uten stopp, men med melding */
    public ExceptionEntry(Route route, LocalDate validDate,
                          Season season, LocalTime departureTime,
                          ExceptionType type, OperationMessage operationMessage) {
        // stopper = null betyr at unntaket gjelder alle stopp
        this(0, route, null, validDate, null, season, departureTime, type, true, operationMessage);
    }

    /** Uten stopp og uten melding */
    public ExceptionEntry(Route route, LocalDate validDate,
                          Season season, LocalTime departureTime,
                          ExceptionType type) {
        this(0, route, null, validDate, null, season, departureTime, type, true, null);
    }

    // --- Ukedag-baserte konstruktører ---
    // Disse brukes når unntaket gjelder en ukedag innenfor en sesong (weekday + season)
    /** Med stopp og melding */
    public ExceptionEntry(Route route, Stop stop, Weekday weekday,
                          Season season, LocalTime departureTime,
                          ExceptionType type, OperationMessage operationMessage) {
        // validDate = null siden dette er ukedag-basert
        this(0, route, stop, null, weekday, season, departureTime, type, true, operationMessage);
    }

    /** Med stopp, uten melding */
    public ExceptionEntry(Route route, Stop stop, Weekday weekday,
                          Season season, LocalTime departureTime,
                          ExceptionType type) {
        this(0, route, stop, null, weekday, season, departureTime, type, true, null);
    }

    /** Uten stopp, men med melding */
    public ExceptionEntry(Route route, Weekday weekday,
                          Season season, LocalTime departureTime,
                          ExceptionType type, OperationMessage operationMessage) {
        this(0, route, null, null, weekday, season, departureTime, type, true, operationMessage);
    }

    /** Uten stopp og uten melding */
    public ExceptionEntry(Route route, Weekday weekday,
                          Season season, LocalTime departureTime,
                          ExceptionType type) {
        this(0, route, null, null, weekday, season, departureTime, type, true, null);
    }

    // --- Validering ---
    /**
     * Validerer at unntaket har enten validDate eller weekday (men ikke begge) og at departureTime er satt.
     */
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

    // --- Metoder for å sjekke om unntaket gjelder en dato eller stopp ---
    /**
     * Sjekker om unntaket gjelder for en gitt dato.
     * Returnerer false hvis unntaket ikke er aktivt.
     */
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

    // --- Overrides ---
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
