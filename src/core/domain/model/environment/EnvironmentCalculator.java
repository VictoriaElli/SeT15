package core.domain.model.environment;

public class EnvironmentCalculator {


    // Funksjon som regner ut hvor mye penger og hvor mye CO2 som er spart på å ta ferge istedenfor privatbil

    public static void calculateWhatYouHaveSaved(DistanceBetweenStops distanceBetweenStops) {

        // Regner ut bom + distansepenger
        double moneySaved = (distanceBetweenStops.tollgateCost() + distanceBetweenStops.distanceCost()) - EnvironmentVariables.ferryRate;

        // Skriver ut ruta
        System.out.println("På ruten " + distanceBetweenStops.from + "-" + distanceBetweenStops.destination + " har du spart:");

        // Skriver ut hvor mye penger som totalt er spart i nærmeste heltall

        System.out.println(java.lang.Math.round(moneySaved) + " kr.");

        // Skriver ut hvor mye C02 som er spart i nærmeste heltall

        System.out.println(java.lang.Math.round(distanceBetweenStops.emission()) + " gram CO2.");
    }


}
