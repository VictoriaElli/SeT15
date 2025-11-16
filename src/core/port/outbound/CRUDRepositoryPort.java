package port.outbound;

import java.util.List;
import java.util.Optional;

public interface CRUDRepositoryPort<T> {
    void create(T entity);

    Optional<T> readById(int id);
    List<T> readAll();

    void update(T entity);

    void delete(T entity);
    void deleteById(int id);
}
