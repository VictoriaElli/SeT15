package core.domain.model.environment;

public class DistanceBetweenStops {

    public String from;
    public String destination;
    public double distance;
    public boolean tollgate;


    // Konstruktør
    public DistanceBetweenStops(String from, String destination, double distance, boolean tollgate) {
        this.from = from;
        this.destination = destination;
        this.distance = distance;
        this.tollgate = tollgate;
    }


    // Funksjon for å finne ut om bom skal betales, og hvis det skal betales så koster det averageCostThruTollgate

    public double tollgateCost(){
        return this.tollgate ? EnvironmentVariables.averageCostThruTollgate : 0;
    }

    // Funksjon for å finne ut hvor mye man sparer på å ta ferga etter hvor mange kilometer det er mellom stoppene
    // Distanse * 3.5 kr per kilometer

    public double distanceCost() {
        return this.distance * EnvironmentVariables.standardRatePrKm;
    }

    // Funksjon for å finne ut hvor mye CO2 man sparer på å kjøre ferga istedenfor en gjennomsnittlig bil
    // Distanse * gjennomsnittlig CO2 utslipp

    public double emission() {
        return this.distance * EnvironmentVariables.averageEmissionPrKm;
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
