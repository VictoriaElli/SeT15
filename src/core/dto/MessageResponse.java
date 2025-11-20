package dto;

import domain.model.OperationMessage;

// Dette er et DTO som brukes for å sende svar tilbake til frontend etter at en driftsmelding er lagret
// MessageResponse brukes for å gi frontend informasjon om det gikk bra eller om det oppstod en feil
public class MessageResponse {

    public boolean success;
    public String error;
    public int id;
    public String status;

    // Disse feltene brukes når vi sender tilbake detaljer om en driftsmelding
    public String message;
    public boolean isActive;
    public int routeId;
    public String routeName;
    public String validFrom;
    public String validTo;
    public String published;

    // Denne brukes når lagringen gikk bra og vi vil sende tilbake id på meldingen
    public static MessageResponse ok(int id) {
        MessageResponse r = new MessageResponse();
        r.success = true;
        r.id = id;
        r.status = "CREATED";
        return r;
    }

    // Denne brukes når vi skal sende tilbake en full driftsmelding
    public static MessageResponse fromMessage(OperationMessage msg) {
        MessageResponse r = new MessageResponse();
        r.success = true;
        r.status = "OK";

        r.id = msg.getId();
        r.message = msg.getMessage();
        r.isActive = msg.isActive();
        r.routeId = msg.getRoute().getId();
        r.routeName = msg.getRoute().getName();
        r.validFrom = msg.getValidFrom().toLocalDate().toString();
        r.validTo = msg.getValidTo() != null ? msg.getValidTo().toLocalDate().toString() : null;
        r.published = msg.getPublished().toLocalDate().toString();

        return r;
    }

    // Denne brukes hvis noe gikk galt og vi må sende feilmelding til frontend
    public static MessageResponse fail(String msg) {
        MessageResponse r = new MessageResponse();
        r.success = false;
        r.error = msg;
        r.status = "ERROR";
        return r;
    }
}
