package org.byferge.core.domain.model.environment;

// resultatet av miljøkalkulasjon, som inkluderer utslipp og kostnader for bil og ferge
public class Result {
    private double carCo2;
    private double ferryCo2;
    private double co2Savings;
    private double carCost;
    private double ferryCost;
    private double costSavings;

    // Constructors
    public Result(double carCo2, double ferryCo2, double carCost, double ferryCost) {
        this.carCo2 = carCo2;
        this.ferryCo2 = ferryCo2;
        this.co2Savings = carCo2 - ferryCo2;
        this.carCost = carCost;
        this.ferryCost = ferryCost;
        this.costSavings = carCost - ferryCost;
    }


    // Getters
    public double getCarCo2() {
        return carCo2;
    }

    public double getFerryCo2() {
        return ferryCo2;
    }

    public double getCo2Savings() {
        return co2Savings;
    }

    public double getFerryCost() {
        return ferryCost;
    }

    public double getCostSavings() {
        return costSavings;
    }


    // Setters
    public void setCarCo2(double carCo2) {
        this.carCo2 = carCo2;
    }

    public void setFerryCo2(double ferryCo2) {
        this.ferryCo2 = ferryCo2;
    }

    public void setCo2Savings(double co2Savings) {
        this.co2Savings = co2Savings;
    }

    public void setFerryCost(double ferryCost) {
        this.ferryCost = ferryCost;
    }

    public void setCostSavings(double costSavings) {
        this.costSavings = costSavings;
    }
}
