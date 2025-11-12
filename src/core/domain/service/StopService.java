package domain.service;

import domain.model.Stop;
import domain.repository.StopRepository;
import java.util.List;

public class StopService {
    private final StopRepository repository;

    public StopService(StopRepository repository) {
        this.repository = repository;
    }

    public void addStop(Stop stop) {
        repository.create(stop);
    }

    public Stop getStop(int id) {
        return repository.read(id);
    }

    public List<Stop> getAllStops() {
        return repository.readAll();
    }

    public void updateStop(Stop stop) {
        repository.update(stop);
    }

    public void removeStop(int id) {
        repository.delete(id);
    }
}