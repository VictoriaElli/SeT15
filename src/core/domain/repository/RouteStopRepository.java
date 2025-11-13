package domain.repository;

import domain.model.RouteStop;
import java.util.List;

public interface RouteStopRepository {
    void create(RouteStop routeStop);           // CREATE
    RouteStop read(int id);                     // READ by ID
    List<RouteStop> readAll();                  // READ all
    void update(RouteStop routeStop);           // UPDATE
    void delete(int id);                        // DELETE by ID
}