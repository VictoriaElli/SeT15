package port.outbound;

import domain.model.Stops;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StopsRepositoryPort extends CRUDRepositoryPort<Stops>{
    Optional<Stops> findByName(String stopName);
    List<Stops> findAllActive();
    List<Stops> findNear(double latitude, double longitude, double radiusKm);
}
