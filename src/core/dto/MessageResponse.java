package dto;

// Dette er et DTO som brukes for å sende svar tilbake til frontend etter at en driftsmelding er lagret
// MessageResponse brukes for å gi frontend informasjon om det gikk bra eller om det oppstod en feil
public class MessageResponse {

    public boolean success;
    public String error;
    public int id;
    public String status;

    // Denne brukes når lagringen gikk bra og vi vil sende tilbake id på meldingen
    public static MessageResponse ok(int id) {
        MessageResponse r = new MessageResponse();
        r.success = true;
        r.id = id;
        r.status = "CREATED";
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
