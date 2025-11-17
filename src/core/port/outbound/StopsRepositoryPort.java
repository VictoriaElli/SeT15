package port.outbound;

import domain.model.Stops;

import java.util.List;
import java.util.Optional;

public interface StopsRepositoryPort extends CRUDRepositoryPort<Stops>{
    Optional<Stops> findByName(String stopName);
    List<Stops> findAllActive();
    List<Stops> findNear(double latitude, double longitude, double radiusKm);
}
