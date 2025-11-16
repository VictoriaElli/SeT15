package domain.repository;

import domain.model.Season;
import java.util.List;

public interface SeasonRepository {
    void create(Season season);           // CREATE
    Season read(int id);                  // READ by ID
    List<Season> readAll();               // READ all
    void update(Season season);           // UPDATE
    void delete(int id);                  // DELETE by ID
}