package port.outbound;

import domain.model.environment.DistanceBetweenStops;
import org.springframework.stereotype.Repository;

@Repository
public interface StopDistanceRepositoryPort extends CRUDRepositoryPort<DistanceBetweenStops> {
    DistanceBetweenStops findByFromAndTo(int fromStopId, int destinationStopId);
}
