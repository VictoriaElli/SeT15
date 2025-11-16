package port.outbound;

import java.util.List;
import java.util.Optional;

public interface FerryRepositoryPort<T> {
    Optional<T> findByName(String name);
    List<T> findAllActive();
}
