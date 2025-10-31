package domain;

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
