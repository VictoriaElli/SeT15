package domain.service;

import domain.model.Route;
import domain.repository.RouteRepository;
import java.util.List;

public class RouteService {
    private final RouteRepository repository;

    public RouteService(RouteRepository repository) {
        this.repository = repository;
    }

    public void addRoute(Route route) {
        repository.create(route);
    }

    public Route getRoute(int id) {
        return repository.read(id);
    }

    public List<Route> getAllRoutes() {
        return repository.readAll();
    }

    public void updateRoute(Route route) {
        repository.update(route);
    }

    public void removeRoute(int id) {
        repository.delete(id);
    }
}