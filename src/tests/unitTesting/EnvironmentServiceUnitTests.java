package unitTesting;

import domain.model.environment.EnvironmentVariables;
import dto.EnvironmentDTO;
import domain.model.environment.DistanceBetweenStops;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import port.outbound.StopDistanceRepositoryPort;
import service.EnvironmentService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EnvironmentServiceUnitTests {

    private StopDistanceRepositoryPort stopDistanceRepository;
    private EnvironmentService environmentService;

    @BeforeEach
    void setUp() {
        stopDistanceRepository = mock(StopDistanceRepositoryPort.class);
        environmentService = new EnvironmentService(stopDistanceRepository);
    }

    @Test
    @DisplayName("calculateSavings returns correct values when tollgate is present")
    void calculateSavings_WithTollgate() {
        DistanceBetweenStops route = new DistanceBetweenStops();
        route.setDistance(10); // km
        route.setTollgate(true);

        when(stopDistanceRepository.findByFromAndTo(1, 2)).thenReturn(route);

        EnvironmentDTO result = environmentService.calculateSavings(1, 2);

        assertNotNull(result);

        double expectedCost = Math.round(EnvironmentVariables.averageCostThruTollgate
                + route.getDistance() * EnvironmentVariables.standardRatePrKm
                - EnvironmentVariables.ferryRate);
        double expectedEmission = Math.round(route.getDistance() * EnvironmentVariables.averageEmissionPrKm);

        assertEquals(expectedCost, result.getCostSaved(), "Cost saved should match expected calculation");
        assertEquals(expectedEmission, result.getEmissionSaved(), "Emission saved should match expected calculation");
    }

    @Test
    @DisplayName("calculateSavings throws exception when route is not found")
    void calculateSavings_RouteNotFound() {
        when(stopDistanceRepository.findByFromAndTo(1, 2)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> environmentService.calculateSavings(1, 2));

        assertEquals("Route not found: 1 -> 2", exception.getMessage());
    }

    @Test
    @DisplayName("calculateSavings returns correct values when no tollgate")
    void calculateSavings_WithoutTollgate() {
        DistanceBetweenStops route = new DistanceBetweenStops();
        route.setDistance(20);
        route.setTollgate(false);

        when(stopDistanceRepository.findByFromAndTo(5, 6)).thenReturn(route);

        EnvironmentDTO result = environmentService.calculateSavings(5, 6);

        double expectedCost = Math.round(0 + route.getDistance() * EnvironmentVariables.standardRatePrKm
                - EnvironmentVariables.ferryRate);
        double expectedEmission = Math.round(route.getDistance() * EnvironmentVariables.averageEmissionPrKm);

        assertEquals(expectedCost, result.getCostSaved(), "Cost saved should match calculation without tollgate");
        assertEquals(expectedEmission, result.getEmissionSaved(), "Emission saved should match calculation without tollgate");
    }

    @Test
    @DisplayName("calculateSavings returns correct values for zero distance")
    void calculateSavings_ZeroDistance() {
        DistanceBetweenStops route = new DistanceBetweenStops();
        route.setDistance(0);
        route.setTollgate(false);

        when(stopDistanceRepository.findByFromAndTo(10, 11)).thenReturn(route);

        EnvironmentDTO result = environmentService.calculateSavings(10, 11);

        double expectedCost = Math.round(0 + 0 * EnvironmentVariables.standardRatePrKm
                - EnvironmentVariables.ferryRate);
        double expectedEmission = 0;

        assertEquals(expectedCost, result.getCostSaved());
        assertEquals(expectedEmission, result.getEmissionSaved());
    }
}
