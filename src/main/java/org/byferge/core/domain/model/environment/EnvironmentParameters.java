package org.byferge.core.domain.model.environment;

import java.time.LocalDate;

// holder miljøparametere som utslipp og kostnader per kilometer for bil og ferge
public class EnvironmentParameters {
    private int id;
    private double carCo2PerKm;
    private double carCostPerKm;
    private double ferryCo2PerKm;
    private double ferryCostPerTrip;
    private LocalDate validFrom;
    private LocalDate validTo;

    // Constructors
    public EnvironmentParameters(int id,double carCo2PerKm, double carCostPerKm, double ferryCo2PerKm, double ferryCostPerTrip, LocalDate validFrom, LocalDate validTo) {
        this.id = id;
        this.carCo2PerKm = carCo2PerKm;
        this.carCostPerKm = carCostPerKm;
        this.ferryCo2PerKm = ferryCo2PerKm;
        this.ferryCostPerTrip = ferryCostPerTrip;
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    // Getters
    public int getId() {
        return id;
    }

    public double getCarCo2PerKm() {
        return carCo2PerKm;
    }

    public double getCarCostPerKm() {
        return carCostPerKm;
    }

    public double getFerryCo2PerKm() {
        return ferryCo2PerKm;
    }

    public double getferryCostPerTrip() {
        return ferryCostPerTrip;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCarCo2PerKm(double carCo2PerKm) {
        this.carCo2PerKm = carCo2PerKm;
    }

    public void setCarCostPerKm(double carCostPerKm) {
        this.carCostPerKm = carCostPerKm;
    }

    public void setFerryCo2PerKm(double ferryCo2PerKm) {
        this.ferryCo2PerKm = ferryCo2PerKm;
    }

    public void setferryCostPerTrip(double ferryCostPerTrip) {
        this.ferryCostPerTrip = ferryCostPerTrip;
    }

    // Overrides
    @Override
    public String toString() {
        return "EnvironmentParameters{" +
                "carCo2PerKm=" + carCo2PerKm +
                ", carCostPerKm=" + carCostPerKm +
                ", ferryCo2PerKm=" + ferryCo2PerKm +
                ", ferryCostPerTrip=" + ferryCostPerTrip +
                '}';
    }
}
