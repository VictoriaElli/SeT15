package org.byferge.core.domain.model.environment;

public class Functions {


    // Funksjon som regner ut hvor mye penger og hvor mye CO2 som er spart på å ta ferge istedenfor privatbil

    public static void calculateWhatYouHaveSaved(Route route) {

        // Regner ut bom + distansepenger
        double moneySaved = (route.tollgateCost() + route.distanceCost()) - Variables.ferryRate;

        // Skriver ut ruta
        System.out.println("På ruten " + route.from + "-" + route.destination + " har du spart:");

        // Skriver ut hvor mye penger som totalt er spart i nærmeste heltall

        System.out.println(java.lang.Math.round(moneySaved) + " kr.");

        // Skriver ut hvor mye C02 som er spart i nærmeste heltall

        System.out.println(java.lang.Math.round(route.emission()) + " gram CO2.");
    }


}
