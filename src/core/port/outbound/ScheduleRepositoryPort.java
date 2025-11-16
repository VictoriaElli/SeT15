package port.outbound;

import domain.model.Route;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ScheduleRepositoryPort<T> {
    Optional<T> findByRouteAndDate(Route route, LocalDate date);
    List<T> findByRoute(Route route);
    List<T> findAllActiveOn(LocalDate date);

}
