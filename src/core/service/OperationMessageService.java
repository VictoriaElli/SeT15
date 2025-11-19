package service;

import dto.MessageRequest;
import dto.MessageResponse;
import domain.model.OperationMessage;
import domain.model.Route;
import port.outbound.OperationMessageRepositoryPort;
import port.outbound.RouteRepositoryPort;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Dette er en service som gjør all validering og logikk for driftsmeldinger
@Service
public class OperationMessageService {

    private static final int MAX_MESSAGE_LENGTH = 500;

    private final OperationMessageRepositoryPort messageRepo;
    private final RouteRepositoryPort routeRepo;

    // Dette er konstruktøren som gjør klar repository for service
    public OperationMessageService(
            @Qualifier("operationMessageRepositoryMYSQLAdapter") OperationMessageRepositoryPort messageRepo,
            @Qualifier("routeRepositoryMYSQLAdapter") RouteRepositoryPort routeRepo) {
        this.messageRepo = messageRepo;
        this.routeRepo = routeRepo;
    }

    // Dette oppretter en ny driftsmelding
    public MessageResponse createMessage(MessageRequest req) {
        try {
            // Bygger og validerer meldingen før lagring
            OperationMessage msg = buildAndValidateMessage(req, null);

            // Lagrer meldingen i databasen
            messageRepo.create(msg);

            // For å få id for en ny melding
            return MessageResponse.ok(msg.getId());

        } catch (IllegalArgumentException e) {
            // Dette skjer når en feil som oppstår ved validering
            return MessageResponse.fail(e.getMessage());

        } catch (Exception e) {
            // Får dette ved uventet feil
            return MessageResponse.fail("Klarte ikke å opprette driftsmelding");
        }
    }

    // Henter en melding og bygger en MessageResponse
    public MessageResponse getMessageAsResponse(int id) {
        Optional<OperationMessage> opt = messageRepo.readById(id);

        if (opt.isEmpty()) {
            return MessageResponse.fail("Melding finnes ikke");
        }

        return MessageResponse.fromMessage(opt.get());
    }

    // Dette henter en melding basert på id
    public OperationMessage getMessage(int id) {
        return messageRepo.readById(id).orElse(null);
    }

    // Henter alle meldinger som ferdige MessageResponse
    public List<MessageResponse> getAllMessagesAsResponse() {

        List<OperationMessage> list = messageRepo.readAll();
        List<MessageResponse> result = new ArrayList<>();

        for (OperationMessage msg : list) {
            result.add(MessageResponse.fromMessage(msg));
        }

        return result;
    }

    // For også hente alle driftsmeldinger
    public List<OperationMessage> getAllMessages() {
        return messageRepo.readAll();
    }

    // Dette er for å oppdatere melding
    public MessageResponse updateMessage(int id, MessageRequest req) {
        try {
            // Sjekker at meldingen finnes
            Optional<OperationMessage> existingOpt = messageRepo.readById(id);
            if (existingOpt.isEmpty()) {
                return MessageResponse.fail("Melding finnes ikke");
            }

            OperationMessage existing = existingOpt.get();

            // Bygger ny melding basert på input, men beholder published fra eksisterende
            OperationMessage updated = buildAndValidateMessage(req, existing);

            // Setter id så riktig melding kan bli oppdatert
            updated.setId(existing.getId());

            // For å oppdaterer meldingen i databasen
            messageRepo.update(updated);

            return MessageResponse.ok(id);

        } catch (IllegalArgumentException e) {
            return MessageResponse.fail(e.getMessage());

        } catch (Exception e) {
            return MessageResponse.fail("Klarte ikke å oppdatere driftsmelding");
        }
    }

    // Dette sletter en melding basert på id
    public MessageResponse deleteMessage(int id) {
        try {
            // Sjekker at meldingen finnes før sletting
            Optional<OperationMessage> existingOpt = messageRepo.readById(id);
            if (existingOpt.isEmpty()) {
                return MessageResponse.fail("Melding finnes ikke");
            }

            // Sletter meldingen
            messageRepo.deleteById(id);

            return MessageResponse.ok(id);

        } catch (Exception e) {
            return MessageResponse.fail("Klarte ikke å slette driftsmelding");
        }
    }

    // Dette bygger og validerer en driftsmelding fra MessageRequest
    private OperationMessage buildAndValidateMessage(MessageRequest req, OperationMessage existing) {

        // Sjekker at teksten ikke er tom eller alt for lang
        if (req.message == null || req.message.isBlank()) {
            throw new IllegalArgumentException("Teksten kan ikke være tom");
        }
        if (req.message.length() > MAX_MESSAGE_LENGTH) {
            throw new IllegalArgumentException("Teksten kan ikke ha mer enn " + MAX_MESSAGE_LENGTH + " tegn");
        }

        // For å se om ruteId er gyldig
        if (req.routeId <= 0) {
            throw new IllegalArgumentException("Ugyldig ruteId");
        }

        // Skal hente en rute fra databasen
        Optional<Route> routeOpt = routeRepo.readById(req.routeId);
        if (routeOpt.isEmpty()) {
            throw new IllegalArgumentException("Rute finnes ikke");
        }
        Route route = routeOpt.get();

        // Sjekker at startdato er satt
        if (req.validFrom == null || req.validFrom.isBlank()) {
            throw new IllegalArgumentException("Startdato må være satt");
        }

        // Det her sjekker at datoformatet er riktig
        try {
            LocalDate.parse(req.validFrom);
            if (req.validTo != null && !req.validTo.isBlank()) {
                LocalDate.parse(req.validTo);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Datoformat må være yyyy-MM-dd");
        }

        // Her blir det sjekket at startdato er før sluttdato
        if (req.validTo != null && !req.validTo.isBlank()) {
            if (req.validFrom.compareTo(req.validTo) >= 0) {
                throw new IllegalArgumentException("Startdato må være før sluttdato");
            }
        }

        // Er for å sette opp riktig datoverdier
        LocalDateTime from = LocalDate.parse(req.validFrom).atStartOfDay();
        LocalDateTime to = null;
        if (req.validTo != null && !req.validTo.isBlank()) {
            to = LocalDate.parse(req.validTo).atTime(23, 59);
        }

        // Ved en oppdatering bruker vi published fra den meldingen som alerede finnes
        if (existing != null) {
            return new OperationMessage(existing.getId(), req.message, existing.getPublished(), route, from, to);
        }

        // Lager en ny melding vis det ikke er en oppdatering
        return new OperationMessage(req.message, req.isActive, route, from, to);
    }
}