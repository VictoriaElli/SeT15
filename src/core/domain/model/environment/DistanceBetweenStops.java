package domain.model.environment;

import domain.model.Stops;

public class DistanceBetweenStops {
    private int id;
    private Stops fromStop;
    private Stops toStop;
    private double distance;
    private boolean tollgate;
    private double emissionSaved;
    private double costSaved;


    // Konstruktør
    public DistanceBetweenStops(Stops from, Stops toStop, double distance, boolean tollgate) {
        this.fromStop = from;
        this.toStop = toStop;
        this.distance = distance;
        this.tollgate = tollgate;

        // Funksjonene som skal være med på JSON-formatet
        this.emissionSaved = emissionSaved();
        this.costSaved = costSaved();
    }

    public DistanceBetweenStops(int id, Stops from, Stops toStop, double distance, boolean tollgate) {
        this.id = id;
        this.fromStop = from;
        this.toStop = toStop;
        this.distance = distance;
        this.tollgate = tollgate;

        // Funksjonene som skal være med på JSON-formatet
        this.emissionSaved = emissionSaved();
        this.costSaved = costSaved();
    }

    // Tom konstruktør for Jackson
    public DistanceBetweenStops() {
    }


    // Funksjon for å finne ut om bom skal betales, og hvis det skal betales så koster det averageCostThruTollgate
    // Hvis tollgate = false, så skal det ikke regne med bompenger
    public double tollgateCostSaved(){
        return this.tollgate ? EnvironmentVariables.averageCostThruTollgate : 0;
    }

    // Funksjon for å finne ut hvor mye man sparer på å ta ferga etter hvor mange kilometer det er mellom stoppene
    // Distanse * 3.5 kr per kilometer
    public double distanceCostSaved() {
        return this.distance * EnvironmentVariables.standardRatePrKm;
    }

    // Funksjon for å finne ut hvor mye CO2 man sparer på å kjøre ferga istedenfor en gjennomsnittlig bil
    // Distanse * gjennomsnittlig CO2 utslipp, rundet til nærmeste heltall
    // JsonProperty for å kunne transformere resultatet av funksjonen til JSON-format

    public double emissionSaved() {
        return java.lang.Math.round(this.distance * EnvironmentVariables.averageEmissionPrKm);
    }

    // Funksjson som regner total kostnad spart på å ta ferge istedenfor bil
    // bompengerspart + distansepenger spart
    // JsonProperty for å kunne transformere resultatet av funksjonen til JSON-format

    public double costSaved() {
        return java.lang.Math.round((tollgateCostSaved() + distanceCostSaved()) - EnvironmentVariables.ferryRate);
    }


    // Gettere og settere


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Stops getFromStop() {
        return fromStop;
    }

    public void setFromStop(Stops fromStop) {
        this.fromStop = fromStop;
    }

    public Stops getToStop() {
        return toStop;
    }

    public void setToStop(Stops toStop) {
        this.toStop = toStop;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public boolean isTollgate() {
        return tollgate;
    }

    public void setTollgate(boolean tollgate) {
        this.tollgate = tollgate;
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
}
