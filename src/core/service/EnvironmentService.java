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
        DistanceBetweenStops calculationRoute = new DistanceBetweenStops(route.getFromStop(),route.getToStop(), route.getDistance(), route.isTollgate());

        double tollSaved = calculationRoute.tollgateCostSaved();
                // route.isTollgate() ? EnvironmentVariables.averageCostThruTollgate : 0;
        double distanceSaved = calculationRoute.distanceCostSaved();
                // route.getDistance() * EnvironmentVariables.standardRatePrKm;
        double totalCostSaved = calculationRoute.getCostSaved();
                // Math.round(tollSaved + distanceSaved - EnvironmentVariables.ferryRate);
        double emissionSaved = calculationRoute.emissionSaved();
                // Math.round(route.getDistance() * EnvironmentVariables.averageEmissionPrKm);

        return new EnvironmentDTO(totalCostSaved, emissionSaved);
    }
}
