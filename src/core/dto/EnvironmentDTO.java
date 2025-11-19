package dto;

public class EnvironmentDTO {

    private double emissionSaved;
    private double costSaved;

    public EnvironmentDTO(double costSaved, double emissionSaved) {
        this.costSaved = costSaved;
        this.emissionSaved = emissionSaved;
    }

    public double getEmissionSaved() {
        return emissionSaved;
    }

    public void setEmissionSaved(double emissionSaved) {
        this.emissionSaved = emissionSaved;
    }

    public double getCostSaved() {
        return costSaved;
    }

    public void setCostSaved(double costSaved) {
        this.costSaved = costSaved;
    }

    @Override
    public String toString() {
        return String.format("Saved %.2f kr & %.2f g CO2", costSaved, emissionSaved);
    }


}

