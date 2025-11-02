package core.domain.model.environment;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DistanceBetweenStops {

    private String from;
    private String destination;
    private double distance;
    private boolean tollgate;


    // Konstruktør
    public DistanceBetweenStops(String from, String destination, double distance, boolean tollgate) {
        this.from = from;
        this.destination = destination;
        this.distance = distance;
        this.tollgate = tollgate;

        // Funksjonene som skal være med på JSON-formatet
        emissionSaved();
        costSaved();
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
    @JsonProperty
    public double emissionSaved() {
        return java.lang.Math.round(this.distance * EnvironmentVariables.averageEmissionPrKm);
    }

    // Funksjson som regner total kostnad spart på å ta ferge istedenfor bil
    // bompengerspart + distansepenger spart
    // JsonProperty for å kunne transformere resultatet av funksjonen til JSON-format
    @JsonProperty
    public double costSaved() {
        return java.lang.Math.round((tollgateCostSaved() + distanceCostSaved()) - EnvironmentVariables.ferryRate);
    }


    // Gettere og settere

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
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


}
