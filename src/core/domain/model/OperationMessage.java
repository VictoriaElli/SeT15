package domain.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

// representerer en driftmelding som gjelder for en bestemt rute innenfor et gitt tidsrom
// inneholder informasjon om meldingtekst, publiseringtidspunkt, gyldighetsperiode og status
public class OperationMessage {
    private int id;
    private String message;
    private final LocalDateTime published;
    private boolean isActive = true;
    private Route route;
    private LocalDateTime validFrom;
    private LocalDateTime validTo;

    // formaterer dato og tid på ønsket måte
    private static final DateTimeFormatter DATE_ONLY = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    // Constructors
    // brukes for eksisterende driftmeldinger med ID i systemet
    public OperationMessage(int id, String message, LocalDateTime published,
                            Route route, LocalDateTime validFrom, LocalDateTime validTo) {
        this.id = id;
        // validerer message
        setMessage(message);
        this.published = published != null ? published : LocalDateTime.now();
        // Bruker setRoute() i stedet for this.route = route
        // Dette er for å sikre at alle driftsmeldinger alltid er knyttet til en bestemt rute
        setRoute(route);
        setValidFrom(validFrom);
        setValidTo(validTo);
    }

    // brukes for nye driftmeldinger uten id
    public OperationMessage(String message, boolean isActive, Route route,
                            LocalDateTime validFrom, LocalDateTime validTo) {
        this(0, message, LocalDateTime.now(), route, validFrom, validTo);
        this.isActive = isActive;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getPublished() {
        return published;
    }

    public boolean isActive() {
        return isActive;
    }

    public Route getRoute() {
        return route;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }


    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setMessage(String message) {
        // hindrer at message er tom. hvis den er det sendes det en feilmelding
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        this.message = message.trim();
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    // Sikrer at alle driftsmeldinger har en rute.
    // Feilmelding oppstår dersom man oppretter en feilmelding uten rute
    public void setRoute(Route route) {
        if (route == null) throw new IllegalArgumentException("Route cannot be null");
        this.route = route;
    }

    // setter gyldighetsstart og validerer at den er før slutt, hvis satt.
    public void setValidFrom(LocalDateTime validFrom) {
        if (validFrom == null) {
            throw new IllegalArgumentException("validFrom cannot be null");
        }
        if (validTo != null && validFrom.isAfter(validTo)) {
            throw new IllegalArgumentException("validFrom must be before or equal to validTo");
        }
        this.validFrom = validFrom;
    }

    // setter gyldighetsslutt og validerer forholdet til startdato. hvis null, settes lik startdato (minimum ett tidspunkt må finnes).
    public void setValidTo(LocalDateTime validTo) {
        if (validTo == null) {
            if (this.validFrom == null) {
                throw new IllegalArgumentException("validFrom must be set before setting validTo to null");
            }
            this.validTo = this.validFrom;
        } else {
            if (this.validFrom != null && validTo.isBefore(this.validFrom)) {
                throw new IllegalArgumentException("validTo must be after or equal to validFrom");
            }
            this.validTo = validTo;
        }
    }

    // Overrides
    // returnerer en lesbar streng med relevant informasjon.
    @Override
    public String toString() {
        return String.format(
                "OperationMessage[id=%d, message='%s', published=%s, active=%b, route=%s, validFrom=%s, validTo=%s]",
                id,
                message,
                published.format(DATE_TIME),
                isActive,
                route != null ? route.getName() : "null",
                validFrom != null ? validFrom.format(DATE_ONLY) : "null",
                validTo != null ? validTo.format(DATE_ONLY) : "null"
        );
    }

    // sammenligner alle relevante felter for å vurdere likhet
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OperationMessage)) return false;
        OperationMessage that = (OperationMessage) o;
        return id == that.id &&
                isActive == that.isActive &&
                message.equals(that.message) &&
                Objects.equals(published, that.published) &&
                Objects.equals(route, that.route) &&
                Objects.equals(validFrom, that.validFrom) &&
                Objects.equals(validTo, that.validTo);
    }

    // genererer hash kode baster på de samme feltene som equals
    @Override
    public int hashCode() {
        return Objects.hash(id, message, published, isActive, route, validFrom, validTo);
    }

    // sjekker om meldingen gjelder akkurat nå (eller et gitt tidspunkt)
    public boolean isActiveNow(LocalDateTime time) {
        LocalDateTime t = (time != null) ? time : LocalDateTime.now();
        return isActive
                && validFrom != null
                && validTo != null
                && !t.isBefore(validFrom)
                && !t.isAfter(validTo);
    }

    // sjekker om meldingen gjelder nå for en bestemt rute
    public boolean isVisibleForRoute(int routeId, LocalDateTime time) {
        return isActiveNow(time)
                && this.route != null
                && this.route.getId() == routeId;
    }
}
