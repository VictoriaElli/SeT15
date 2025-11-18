package service;

import domain.model.OperationMessage;
import domain.repository.OperationMessageRepository;
import java.util.List;

// Dette er en service som håndterer logikken for driftsmeldinger
// Den brukes av controlleren og snakker videre med repositoryet
public class OperationMessageService {

    private final OperationMessageRepository repository;

    // Dette er konstruktøren som gjør klar repository slik at service kan bruke det
    public OperationMessageService(OperationMessageRepository repository) {
        this.repository = repository;
    }

    // Dette lagrer en ny driftsmelding via repositoryet
    public void addMessage(OperationMessage message) {
        try {
            repository.create(message);

            // Dette logger hvem som lagret meldingen
            if (message.getCreatedBy() != null) {
                System.out.println("INFO: Meldingen ble lagret av admin: " + message.getCreatedBy());
            }
        } catch (Exception e) {
            System.out.println("FEIL: Klarte ikke å lagre driftsmelding (" + e.getMessage() + ")");
            throw new RuntimeException("Kunne ikke lagre driftsmelding.");
        }
    }

    // Dette henter en spesifikk driftsmelding
    public OperationMessage getMessage(int id) {
        try {
            return repository.read(id);
        } catch (Exception e) {
            System.out.println("FEIL: Klarte ikke å hente driftsmelding (" + e.getMessage() + ")");
            throw new RuntimeException("Kunne ikke hente driftsmelding.");
        }
    }

    // Dette henter alle driftsmeldinger
    public List<OperationMessage> getAllMessages() {
        try {
            return repository.readAll();
        } catch (Exception e) {
            System.out.println("FEIL: Klarte ikke å hente alle driftsmeldinger (" + e.getMessage() + ")");
            throw new RuntimeException("Kunne ikke hente driftsmeldinger.");
        }
    }

    // Dette oppdaterer en eksisterende driftsmelding
    public void updateMessage(OperationMessage message) {
        try {
            repository.update(message);

            // For logging av hvem som oppdaterte meldingen
            if (message.getCreatedBy() != null) {
                System.out.println("INFO: Meldingen ble oppdatert av admin: " + message.getCreatedBy());
            }

        } catch (Exception e) {
            System.out.println("FEIL: Klarte ikke å oppdatere driftsmelding (" + e.getMessage() + ")");
            throw new RuntimeException("Kunne ikke oppdatere driftsmelding.");
        }
    }

    // Dette sletter en driftsmelding basert på id
    public void removeMessage(int id) {
        try {
            repository.delete(id);

            // For logging av hva som er slettet
            System.out.println("INFO: Driftsmelding med id " + id + " ble slettet.");

        } catch (Exception e) {
            System.out.println("FEIL: Klarte ikke å slette driftsmelding (" + e.getMessage() + ")");
            throw new RuntimeException("Kunne ikke slette driftsmelding.");
        }
    }
}



