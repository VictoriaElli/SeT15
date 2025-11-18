package port.outbound;

import domain.model.Route;
import domain.model.RouteStops;
import domain.model.Stops;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteStopsRepositoryPort extends CRUDRepositoryPort<RouteStops> {
    List<RouteStops> findAllActive();
    List<RouteStops> findByRoute(Route route);
    List<RouteStops> findByStop(Stops stop);
}
