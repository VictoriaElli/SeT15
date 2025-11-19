package service;

import domain.model.environment.DistanceBetweenStops;
import domain.model.environment.EnvironmentVariables;
import dto.EnvironmentDTO;
import port.outbound.StopDistanceRepositoryPort;
import org.springframework.stereotype.Service;

@Service
public class EnvironmentService {

    private final StopDistanceRepositoryPort stopDistanceRepository;

    public EnvironmentService(StopDistanceRepositoryPort stopDistanceRepository) {
        this.stopDistanceRepository = stopDistanceRepository;
    }

    /**
     * Beregn miljøbesparelser mellom to stop IDs.
     */
    public EnvironmentDTO calculateSavings(int fromStopId, int toStopId) {
        DistanceBetweenStops route = stopDistanceRepository.findByFromAndTo(fromStopId, toStopId);

        if (route == null) {
            throw new RuntimeException("Route not found: " + fromStopId + " -> " + toStopId);
        }

        double tollSaved = route.isTollgate() ? EnvironmentVariables.averageCostThruTollgate : 0;
        double distanceSaved = route.getDistance() * EnvironmentVariables.standardRatePrKm;
        double totalCostSaved = Math.round(tollSaved + distanceSaved - EnvironmentVariables.ferryRate);
        double emissionSaved = Math.round(route.getDistance() * EnvironmentVariables.averageEmissionPrKm);

        return new EnvironmentDTO(totalCostSaved, emissionSaved);
    }
}
