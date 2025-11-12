package domain.service;

import domain.model.RouteStop;
import domain.repository.RouteStopRepository;
import java.util.List;

public class RouteStopService {
    private final RouteStopRepository repository;

    public RouteStopService(RouteStopRepository repository) {
        this.repository = repository;
    }

    public void addRouteStop(RouteStop routeStop) {
        repository.create(routeStop);
    }

    public RouteStop getRouteStop(int id) {
        return repository.read(id);
    }

    public List<RouteStop> getAllRouteStops() {
        return repository.readAll();
    }

    public void updateRouteStop(RouteStop routeStop) {
        repository.update(routeStop);
    }

    public void removeRouteStop(int id) {
        repository.delete(id);
    }
}