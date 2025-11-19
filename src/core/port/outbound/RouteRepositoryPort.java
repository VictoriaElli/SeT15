package port.outbound;

import domain.model.Route;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RouteRepositoryPort extends CRUDRepositoryPort<Route> {
    Optional<Route> findByRouteName(String routeName);
    List<Route> findAllActive();
}
