package controllers;

import dto.MessageRequest;
import dto.MessageResponse;
import service.OperationMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final OperationMessageService service;

    // Dette er konstruktøren som gjør klar service slik at controlleren kan bruke den
    public MessageController(OperationMessageService service) {
        this.service = service;
    }

    // Dette er endepunktet som brukes for å opprette driftsmeldinger
    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(
            @RequestBody MessageRequest req,
            @RequestHeader(value = "adminToken", required = false) String adminToken) {

        // Dette er for å sikre at kun admin kan opprette meldinger
        if (adminToken == null || !adminToken.equals("test123")) {
            return ResponseEntity.status(401).body(MessageResponse.fail("Ikke autorisert"));
        }

        // Dette er for å sende requesten videre til service som gjør validering og lagring
        MessageResponse response = service.createFullMessage(req);

        // Hvis service fant en feil sendes feil tilbake
        if (!response.success) {
            return ResponseEntity.badRequest().body(response);
        }

        // Vi får 201 (created) når alt gikk bra
        return ResponseEntity.status(201).body(response);
    }

    // Dette henter en melding basert på id,og returnerer MessageResponse
    @GetMapping("/{id}")
    public ResponseEntity<MessageResponse> getMessage(@PathVariable int id) {

        MessageResponse response = service.getMessageAsResponse(id);

        // Er for hvis meldingen ikke finnes
        if (!response.success && response.error != null) {
            return ResponseEntity.status(404).body(response);
        }

        return ResponseEntity.ok(response);
    }

    // Dette henter alle driftsmeldinger som ferdig DTO
    @GetMapping
    public ResponseEntity<List<MessageResponse>> getAllMessages() {
        List<MessageResponse> list = service.getAllMessagesAsResponse();
        return ResponseEntity.ok(list);
    }

    // Dette oppdaterer en melding basert på id
    @PutMapping("/{id}")
    public ResponseEntity<MessageResponse> updateMessage(
            @PathVariable int id,
            @RequestBody MessageRequest req,
            @RequestHeader(value = "adminToken", required = false) String adminToken) {

        // For at kun admin skal kunne oppdatere meldinger
        if (adminToken == null || !adminToken.equals("test123")) {
            return ResponseEntity.status(401).body(MessageResponse.fail("Ikke autorisert"));
        }

        // Er for å sende oppdateringen videre til service
        MessageResponse response = service.updateMessage(id, req);

        // Ved feil returneres badRequest
        if (!response.success) {
            return ResponseEntity.badRequest().body(response);
        }

        // Det returneres OK hvis endringen gikk bra
        return ResponseEntity.ok(response);
    }

    // Dette sletter en melding basert på id
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteMessage(
            @PathVariable int id,
            @RequestHeader(value = "adminToken", required = false) String adminToken) {

        // Er for at kun admin skal kunne slette meldinger
        if (adminToken == null || !adminToken.equals("test123")) {
            return ResponseEntity.status(401).body(MessageResponse.fail("Ikke autorisert"));
        }

        // Dette sender slettingen videre til service
        MessageResponse response = service.deleteMessage(id);

        // Det vil returnere feil hvis sletting feilet
        if (!response.success) {
            return ResponseEntity.badRequest().body(response);
        }

        // Det vil returnere OK hvis sletting gikk bra
        return ResponseEntity.ok(response);
    }
}
