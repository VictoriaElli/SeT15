package port.outbound;

import domain.model.Route;
import domain.model.RouteStops;
import domain.model.Stops;

import java.util.List;

public interface RouteStopsRepositoryPort extends CRUDRepositoryPort<RouteStops> {
    List<RouteStops> findAllActive();
    List<RouteStops> findByRoute(Route route);
    List<RouteStops> findByStop(Stops stop);
}
