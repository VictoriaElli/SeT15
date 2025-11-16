package domain.repository;

import domain.model.Route;
import java.util.List;

public interface RouteRepository {
    void create(Route route);           // CREATE
    Route read(int id);                 // READ by ID
    List<Route> readAll();              // READ all
    void update(Route route);           // UPDATE
    void delete(int id);                // DELETE by ID
}