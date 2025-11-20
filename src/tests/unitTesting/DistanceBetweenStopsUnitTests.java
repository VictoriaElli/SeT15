package unitTesting;

import domain.model.environment.DistanceBetweenStops;
import domain.model.environment.EnvironmentVariables;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;


public class DistanceBetweenStopsUnitTests {


    // Test for å sjekke metoden tollgateCostSaved
    @Test
    @DisplayName("Check tollgateCostSaved when tollgate is true")
    public void correctTollgateCostWhenTollgateIsTrue() {

        // Arrange
        DistanceBetweenStops object = new DistanceBetweenStops();

        // Setter tollgate = true
        object.setTollgate(true);

        // Setter variabelen gjennomsnittlig betaling gjennom bom til 30 kr
        EnvironmentVariables.averageCostThruTollgate = 30;

        // Act
        double result = object.tollgateCostSaved();

        // Assert
        Assertions.assertEquals(30.0 , result);

    }

    // Test for å sjekke metoden tollgateCostSaved
    @Test
    @DisplayName("Check tollgateCostSaved when tollgate is false")
    public void correctTollgateCostWhenTollgateIsFalse() {

        // Arrange
        DistanceBetweenStops object = new DistanceBetweenStops();

        // Setter tollgate = false
        object.setTollgate(false);

        // Setter variabelen gjennomsnittlig betaling gjennom bom til 30 kr
        EnvironmentVariables.averageCostThruTollgate = 30;

        // Act
        double result = object.tollgateCostSaved();

        // Assert
        Assertions.assertEquals(0.0, result);

    }

    // Test for å sjekke metoden distanceCostSaved
    @Test
    @DisplayName("Check distanceCostSaved")
    public void correctDistanceCostSaved() {

        // Arrange
        DistanceBetweenStops object = new DistanceBetweenStops();

        // Setter distansen til 10 km
        object.setDistance(10);

        // Setter variabelen standard betaling per km til 10 kr
        EnvironmentVariables.standardRatePrKm = 10;

        // Act
        double result = object.distanceCostSaved();

        // Assert
        Assertions.assertEquals(100, result);
    }

    // Test for å sjekke metoden emissionSaved
    @Test
    @DisplayName("Check emissionSaved")
    public void correctEmissionSaved() {

        // Arrange
        DistanceBetweenStops object = new DistanceBetweenStops();

        // Setter distansen til 10 km
        object.setDistance(10.00);

        // Setter variabelen gjennomsnittlig CO2 utslipp per km til 10 gram
        EnvironmentVariables.averageEmissionPrKm = 10.00;

        // Act
        double result = object.emissionSaved();

        // Assert
        Assertions.assertEquals(100, result);
    }

    // Test for å sjekke metoden costSaved
    @Test
    @DisplayName("Check costSaved when tollgate is false")
    public void correctCostSavedWhenTollgateIsFalse() {

        // Arrange
        DistanceBetweenStops object = new DistanceBetweenStops();

        // Setter distansen til 10 km
        object.setDistance(10.00);

        // Setter tollgate = false
        object.setTollgate(false);

        // Setter at det koster 10 kr å ta ferga
        EnvironmentVariables.ferryRate = 10;

        // Setter gjennomsnittlig bom til 10 kr
        EnvironmentVariables.averageCostThruTollgate = 10;

        // Setter variabelen standard betaling per km til 10 kr
        EnvironmentVariables.standardRatePrKm = 10;

        // Act
        double result = object.costSaved();

        // Assert
        Assertions.assertEquals(90, result);
    }


    // Test for å sjekke metoden costsaved
    @Test
    @DisplayName("Check costSaved when tollgate is true")
    public void correctCostSavedWhenTollgateIsTrue() {

        // Arrange
        DistanceBetweenStops object = new DistanceBetweenStops();

        // Setter distansen til 10 km
        object.setDistance(10.00);

        // Setter tollgate = true
        object.setTollgate(true);

        // Setter at det koster 10 kr å ta ferga
        EnvironmentVariables.ferryRate = 10;

        // Setter gjennomsnittlig bom til 10 kr
        EnvironmentVariables.averageCostThruTollgate = 10;

        // Setter variabelen standard betaling per km til 10 kr
        EnvironmentVariables.standardRatePrKm = 10;

        // Act
        double result = object.costSaved();

        // Assert
        Assertions.assertEquals(100, result);
    }





}
