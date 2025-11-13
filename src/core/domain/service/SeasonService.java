package domain.service;

import domain.model.Season;
import domain.repository.SeasonRepository;
import java.util.List;

public class SeasonService {
    private final SeasonRepository repository;

    public SeasonService(SeasonRepository repository) {
        this.repository = repository;
    }

    public void addSeason(Season season) {
        repository.create(season);
    }

    public Season getSeason(int id) {
        return repository.read(id);
    }

    public List<Season> getAllSeasons() {
        return repository.readAll();
    }

    public void updateSeason(Season season) {
        repository.update(season);
    }

    public void removeSeason(int id) {
        repository.delete(id);
    }
}