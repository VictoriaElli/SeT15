package domain.service;

import domain.model.ExceptionEntry;
import domain.repository.ExceptionEntryRepository;
import java.util.List;

public class ExceptionEntryService {
    private final ExceptionEntryRepository repository;

    public ExceptionEntryService(ExceptionEntryRepository repository) {
        this.repository = repository;
    }

    public void addException(ExceptionEntry exceptionEntry) {
        repository.create(exceptionEntry);
    }

    public ExceptionEntry getException(int id) {
        return repository.read(id);
    }

    public List<ExceptionEntry> getAllExceptions() {
        return repository.readAll();
    }

    public void updateException(ExceptionEntry exceptionEntry) {
        repository.update(exceptionEntry);
    }

    public void removeException(int id) {
        repository.delete(id);
    }
}