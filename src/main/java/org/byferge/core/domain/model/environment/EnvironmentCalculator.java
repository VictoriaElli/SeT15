package org.byferge.core.domain.model.environment;

// kalkulerer miljøpåvirkning og kostnader basert på distanser og miljøparametere.
public class EnvironmentCalculator {

    public Result calculate(double distanceByCar, double distanceByFerry, EnvironmentParameters params) {
        if (distanceByCar < 0 || distanceByFerry < 0) {
            throw new IllegalArgumentException("Distances cannot be negative");
        }
        if (params == null) {
            throw new IllegalArgumentException("EnvironmentParameters cannot be null");
        }

        double carCo2 = distanceByCar * params.getCarCo2PerKm();
        double ferryCo2 = distanceByFerry * params.getFerryCo2PerKm();

        double carCost = distanceByCar * params.getCarCostPerKm();
        double ferryCost = params.getferryCostPerTrip();

        return new Result(carCo2, carCost, ferryCo2, ferryCost);
    }
}
