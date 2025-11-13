package controllers;

import domain.model.*;
import domain.service.OperationMessageService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import dto.MessageRequest;
import dto.MessageResponse;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final OperationMessageService messageService;

    // Dette er konstruktøren som gjør klar service slik at controlleren kan bruke den
    public MessageController(OperationMessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping
    public void addMessage(@RequestBody OperationMessage message) {
        messageService.addMessage(message);
    }

    @GetMapping("/{id}")
    public OperationMessage getMessage(@PathVariable int id) {
        return messageService.getMessage(id);
    }

    @GetMapping
    public List<OperationMessage> getAllMessages() {
        return messageService.getAllMessages();
    }

    @PutMapping("/{id}")
    public void updateMessage(@PathVariable int id, @RequestBody OperationMessage message) {
        message.setId(id);
        messageService.updateMessage(message);
    }

    @DeleteMapping("/{id}")
    public void deleteMessage(@PathVariable int id) {
        messageService.removeMessage(id);
    }


    // Dette er et endepunkt som brukes for å opprette driftsmeldinger fra UI
    // Endepunktet tar imot data fra MessageRequest, og validerer disse og lagrer meldingen i databasen
    @PostMapping("/create")
    public MessageResponse createMessage(@RequestBody MessageRequest req,
                                         @RequestHeader(value = "adminToken", required = false) String adminToken) {

        // Dette er for å sikre at kun admin kan opprette driftsmeldinger
        if (adminToken == null || !adminToken.equals("test123")) {
            System.out.println("FEIL: Ugyldig adminToken.");
            return MessageResponse.fail("Ikke autorisert");
        }

        // Dette sjekker om tekstfeltet er tomt
        if (req.message == null || req.message.isBlank()) {
            return MessageResponse.fail("Tekst kan ikke være tom");
        }

        // Dette sjekker at ruteId er gyldig
        if (req.routeId <= 0) {
            return MessageResponse.fail("Ugyldig ruteId");
        }

        // Dette sjekker at startdato er satt
        if (req.validFrom == null || req.validFrom.isBlank()) {
            return MessageResponse.fail("Startdato må være satt");
        }

        // Dette sjekker at sluttdato er gyldig om den finnes
        if (req.validTo != null && !req.validTo.isBlank()) {
            if (req.validFrom.compareTo(req.validTo) >= 0) {
                return MessageResponse.fail("Startdato må være før sluttdato");
            }
        }

        try {
            // Dette er for å opprette en ny driftsmelding
            OperationMessage msg = new OperationMessage(req.message, req.isActive, req.routeId);


            // Dette er for å lagre driftsmeldingen i databasen
            messageService.addMessage(msg);

            System.out.println("INFO: Driftsmelding ble opprettet.");

            // Dette returnerer id til meldingen tilbake til frontend
            return MessageResponse.ok(msg.getId());

        } catch (Exception e) {
            System.out.println("FEIL: Klarte ikke å opprette driftsmelding (" + e.getMessage() + ")");
            return MessageResponse.fail("Klarte ikke å opprette driftsmelding");
        }
    }
}

