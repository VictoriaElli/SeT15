package domain.model.environment;

public class EnvironmentCalculator {


    // Funksjon som regner ut hvor mye penger og hvor mye CO2 som er spart på å ta ferge istedenfor privatbil

    public static void calculateWhatYouHaveSaved(DistanceBetweenStops distanceBetweenStops) {

        // Skriver ut ruta
        System.out.println("På ruten " + distanceBetweenStops.getFromStop() + "-" + distanceBetweenStops.getToStop() + " har du spart:");

        // Skriver ut hvor mye penger som totalt er spart i nærmeste heltall

        System.out.println(java.lang.Math.round(distanceBetweenStops.costSaved()) + " kr.");

        // Skriver ut hvor mye C02 som er spart i nærmeste heltall

        System.out.println(java.lang.Math.round(distanceBetweenStops.emissionSaved()) + " gram CO2.");
    }


}
