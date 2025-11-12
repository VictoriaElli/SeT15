package domain.service;

import domain.model.OperationMessage;
import domain.repository.OperationMessageRepository;
import java.util.List;

public class OperationMessageService {
    private final OperationMessageRepository repository;

    public OperationMessageService(OperationMessageRepository repository) {
        this.repository = repository;
    }

    public void addMessage(OperationMessage message) {
        repository.create(message);
    }

    public OperationMessage getMessage(int id) {
        return repository.read(id);
    }

    public List<OperationMessage> getAllMessages() {
        return repository.readAll();
    }

    public void updateMessage(OperationMessage message) {
        repository.update(message);
    }

    public void removeMessage(int id) {
        repository.delete(id);
    }
}