package controllers;

import domain.model.OperationMessage;
import domain.model.Route;
import domain.service.OperationMessageService;
import domain.service.RouteService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import dto.MessageRequest;
import dto.MessageResponse;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    // For max lengde på tekst
    private static final int MAX_MESSAGE_LENGTH = 500;

    private final OperationMessageService messageService;
    private final RouteService routeService;

    // Dette er konstruktøren som gjør klar service slik at controlleren kan bruke den
    public MessageController(OperationMessageService messageService, RouteService routeService) {
        this.messageService = messageService;
        this.routeService = routeService;
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
    @PostMapping
    public ResponseEntity<MessageResponse> createMessage(@RequestBody MessageRequest req,
                                                         @RequestHeader(value = "adminToken", required = false) String adminToken) {

        // Dette er for å sikre at kun admin kan opprette driftsmeldinger
        if (adminToken == null || !adminToken.equals("test123")) {
            System.out.println("FEIL: Ugyldig adminToken.");
            return ResponseEntity.status(401).body(MessageResponse.fail("Ikke autorisert"));
        }

        // Dette sjekker om tekstfeltet er tomt
        if (req.message == null || req.message.isBlank()) {
            return ResponseEntity.badRequest().body(MessageResponse.fail("Tekst kan ikke være tom"));
        }

        // Dette er for å sjekk maksimal tekstlengde
        if (req.message.length() > MAX_MESSAGE_LENGTH) {
            return ResponseEntity.badRequest().body(MessageResponse.fail("Tekst kan ikke være lengre enn " + MAX_MESSAGE_LENGTH + " tegn"));
        }

        // Dette sjekker at ruteId er gyldig
        if (req.routeId <= 0) {
            return ResponseEntity.badRequest().body(MessageResponse.fail("Ugyldig ruteId"));
        }

        // Dette er for å sjekk at ruten faktisk finnes
        Route route = routeService.getRoute(req.routeId);
        if (route == null) {
            return ResponseEntity.badRequest().body(MessageResponse.fail("Rute finnes ikke"));
        }

        // Dette sjekker at startdato er satt
        if (req.validFrom == null || req.validFrom.isBlank()) {
            return ResponseEntity.badRequest().body(MessageResponse.fail("Startdato må være satt"));
        }

        // Validering av datoformat
        try {
            LocalDate.parse(req.validFrom);

            if (req.validTo != null && !req.validTo.isBlank()) {
                LocalDate.parse(req.validTo);
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(MessageResponse.fail("Dato må være på format yyyy-MM-dd"));
        }

        // Dette sjekker at sluttdato er gyldig om den finnes
        if (req.validTo != null && !req.validTo.isBlank()) {
            if (req.validFrom.compareTo(req.validTo) >= 0) {
                return ResponseEntity.badRequest().body(MessageResponse.fail("Startdato må være før sluttdato"));
            }
        }

        try {
            // Dette lager startdato for driftsmeldingen
            LocalDateTime from = LocalDate.parse(req.validFrom).atStartOfDay();

            // Dette lager sluttdato hvis den finnes
            LocalDateTime to = null;
            if (req.validTo != null && !req.validTo.isBlank()) {
                to = LocalDate.parse(req.validTo).atTime(23, 59);
            }

            // Dette er for å opprette en ny driftsmelding
            OperationMessage msg = new OperationMessage(req.message, req.isActive, new Route(req.routeId), from, to);

            // Logger hvem som lagret meldingen
            msg.setCreatedBy(adminToken);

            // Dette er for å lagre driftsmeldingen i databasen
            messageService.addMessage(msg);

            System.out.println("INFO: Driftsmelding ble opprettet.");

            // Dette returnerer id til meldingen tilbake til frontend
            return ResponseEntity.status(201).body(MessageResponse.ok(msg.getId()));

        } catch (Exception e) {
            System.out.println("FEIL: Klarte ikke å opprette driftsmelding (" + e.getMessage() + ")");
            return ResponseEntity.status(500).body(MessageResponse.fail("Klarte ikke å opprette driftsmelding"));
        }
    }

}

