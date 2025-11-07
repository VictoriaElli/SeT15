package unitTesting;

import domain.model.environment.DistanceBetweenStops;
import domain.model.environment.EnvironmentVariables;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;


public class DistanceBetweenStopsTests {

    @Test
    @DisplayName("Check tollgateCostSaved when tollgate is true")
    public void correctTollgateCostWhenTollgateIsTrue() {

        // Arrange
        DistanceBetweenStops object = new DistanceBetweenStops();
        object.setTollgate(true);
        EnvironmentVariables.averageCostThruTollgate = 30;

        // Act
        double result = object.tollgateCostSaved();

        // Assert
        Assertions.assertEquals(30.0 , result);

    }

    @Test
    @DisplayName("Check tollgateCostSaved when tollgate is false")
    public void correctTollgateCostWhenTollgateIsFalse() {

        // Arrange
        DistanceBetweenStops object = new DistanceBetweenStops();
        object.setTollgate(true);
        EnvironmentVariables.averageCostThruTollgate = 30;

        // Act
        double result = object.tollgateCostSaved();

        // Assert
        Assertions.assertEquals(0.0, result);

    }

    @Test
    @DisplayName("Check distanceCostSaved")
    public void correctDistanceCostSaved() {

        // Arrange
        DistanceBetweenStops object = new DistanceBetweenStops();
        object.setDistance(10);
        EnvironmentVariables.standardRatePrKm = 10;

        // Act
        double result = object.distanceCostSaved();

        // Assert
        Assertions.assertEquals(100, result);
    }

    @Test
    @DisplayName("Check emissionSaved")
    public void correctEmissionSaved() {

        // Arrange
        DistanceBetweenStops object = new DistanceBetweenStops();
        object.setDistance(10.00);
        EnvironmentVariables.averageEmissionPrKm = 10.00;

        // Act
        double result = object.emissionSaved();

        // Assert
        Assertions.assertEquals(100, result);
    }





}
