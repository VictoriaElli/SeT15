package domain.repository;

import domain.model.Stop;
import java.util.List;

public interface StopRepository {
    void create(Stop stop);           // CREATE
    Stop read(int id);                // READ by ID
    List<Stop> readAll();             // READ all
    void update(Stop stop);           // UPDATE
    void delete(int id);              // DELETE by ID
}