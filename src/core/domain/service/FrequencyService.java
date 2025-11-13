package domain.service;

import domain.model.Frequency;
import domain.repository.FrequencyRepository;
import java.util.List;

public class FrequencyService {
    private final FrequencyRepository repository;

    public FrequencyService(FrequencyRepository repository) {
        this.repository = repository;
    }

    public void addFrequency(Frequency frequency) {
        repository.create(frequency);
    }

    public Frequency getFrequency(int id) {
        return repository.read(id);
    }

    public List<Frequency> getAllFrequencies() {
        return repository.readAll();
    }

    public void updateFrequency(Frequency frequency) {
        repository.update(frequency);
    }

    public void removeFrequency(int id) {
        repository.delete(id);
    }
}