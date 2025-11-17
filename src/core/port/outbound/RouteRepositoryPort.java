package port.outbound;

import domain.model.Route;

import java.util.List;
import java.util.Optional;

public interface RouteRepositoryPort extends CRUDRepositoryPort<Route> {
    Optional<Route> findByRouteName(String routeName);
    List<Route> findAllActive();
}
