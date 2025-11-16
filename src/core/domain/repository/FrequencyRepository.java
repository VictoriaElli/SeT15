package domain.repository;

import domain.model.Frequency;
import java.util.List;

public interface FrequencyRepository {
    void create(Frequency frequency);           // CREATE
    Frequency read(int id);                     // READ by ID
    List<Frequency> readAll();                  // READ all
    void update(Frequency frequency);           // UPDATE
    void delete(int id);                        // DELETE by ID
}

