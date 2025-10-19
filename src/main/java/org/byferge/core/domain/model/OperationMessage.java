package org.byferge.core.domain.model;

import org.byferge.core.domain.model.Route;

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
        this.message = Objects.requireNonNull(message, "Message cannot be null");
        this.published = published != null ? published : LocalDateTime.now();
        this.route = route;
        setValidFrom(validFrom);
        setValidTo(validTo);
    }

    // brukes for nye driftmeldinger uten id
    public OperationMessage(String message, boolean isActive, Route route,
                            LocalDateTime validFrom, LocalDateTime validTo) {
        this(0, message, LocalDateTime.now(), route, validFrom, validTo);
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
        this.message = message;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public void setRoute(Route route) {
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
}
