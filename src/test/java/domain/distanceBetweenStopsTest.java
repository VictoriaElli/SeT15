package domain;
import org.byferge.core.domain.model.environment.DistanceBetweenStops;
import org.byferge.core.domain.model.environment.EnvironmentVariables;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class enviromentMethodsTest {

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
    



}
