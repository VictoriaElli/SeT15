package app;

import port.outbound.ferryRoutesJSONRepository;
import domain.model.environment.DistanceBetweenStops;


import java.io.File;
import java.util.ArrayList;


public class DistanceBetweenStopsObjects {

    public static void main(String[] args) {


        // Arrayliste som skal holde på alle objektene av DistanceBetweenStops. Altså alle ferge rutene
        ArrayList<DistanceBetweenStops> ferryRoutes = new ArrayList<>();



        // Fra Gamlebyen

        DistanceBetweenStops Gamlebyen_Cicignon = new DistanceBetweenStops("Gamlebyen", "Cicignon", 3.6, true);
        ferryRoutes.add(Gamlebyen_Cicignon);
        DistanceBetweenStops Gamlebyen_Smertu = new DistanceBetweenStops("Gamlebyen", "Smertu", 4.3, true);
        ferryRoutes.add(Gamlebyen_Smertu);
        DistanceBetweenStops Gamlebyen_Sentrum = new DistanceBetweenStops("Gamlebyen", "Sentrum", 4.3, true);
        ferryRoutes.add(Gamlebyen_Sentrum);
        DistanceBetweenStops Gamlebyen_Vaerste = new DistanceBetweenStops("Gamlebyen", "Værste", 5.1, true);
        ferryRoutes.add(Gamlebyen_Vaerste);
        DistanceBetweenStops Gamlebyen_Gressvik = new DistanceBetweenStops("Gamlebyen", "Gressvik", 8.6, true);
        ferryRoutes.add(Gamlebyen_Gressvik);
        DistanceBetweenStops Gamlebyen_Aalekilen = new DistanceBetweenStops("Gamlebyen", "Ålekilen", 9.5, true);
        ferryRoutes.add(Gamlebyen_Aalekilen);

        // Fra Smertu

        DistanceBetweenStops Smertu_Gamlebyen = new DistanceBetweenStops("Smertu", "Gamlebyen", 4.3, false);
        ferryRoutes.add(Smertu_Gamlebyen);
        DistanceBetweenStops Smertu_Sentrum = new DistanceBetweenStops("Smertu", "Sentrum", 2.4, false);
        ferryRoutes.add(Smertu_Sentrum);
        DistanceBetweenStops Smertu_Vaerste= new DistanceBetweenStops("Smertu", "Værste", 1.9, false);
        ferryRoutes.add(Smertu_Vaerste);
        DistanceBetweenStops Smertu_Gressvik= new DistanceBetweenStops("Smertu", "Gressvik", 6.8, false);
        ferryRoutes.add(Smertu_Gressvik);
        DistanceBetweenStops Smertu_Aalekilen= new DistanceBetweenStops("Smertu", "Ålekilen", 7.2, false);
        ferryRoutes.add(Smertu_Aalekilen);

        // Fra Sentrum

        DistanceBetweenStops Sentrum_Gamlebyen = new DistanceBetweenStops("Sentrum", "Gamlebyen", 4.3, false);
        ferryRoutes.add(Sentrum_Gamlebyen);
        DistanceBetweenStops Sentrum_Smertu = new DistanceBetweenStops("Sentrum", "Smertu", 2.4, false);
        ferryRoutes.add(Sentrum_Smertu);
        DistanceBetweenStops Sentrum_Vaerste = new DistanceBetweenStops("Sentrum", "Værste", 2.3, false);
        ferryRoutes.add(Sentrum_Vaerste);
        DistanceBetweenStops Sentrum_Gressvik = new DistanceBetweenStops("Sentrum", "Gressvik", 5.8, false);
        ferryRoutes.add(Sentrum_Gressvik);
        DistanceBetweenStops Sentrum_Aalekilen = new DistanceBetweenStops("Sentrum", "Ålekilen", 6.8, false);
        ferryRoutes.add(Sentrum_Aalekilen);

        // Fra Værste

        DistanceBetweenStops Vaerste_Gamlebyen = new DistanceBetweenStops("Værste", "Gamlebyen", 5.1, false);
        ferryRoutes.add(Vaerste_Gamlebyen);
        DistanceBetweenStops Vaerste_Smertu = new DistanceBetweenStops("Værste", "Smertu", 1.9, false);
        ferryRoutes.add(Vaerste_Smertu);
        DistanceBetweenStops Vaerste_Sentrum = new DistanceBetweenStops("Værste", "Sentrum", 2.3, false);
        ferryRoutes.add(Vaerste_Sentrum);
        DistanceBetweenStops Vaerste_Gressvik = new DistanceBetweenStops("Værste", "Gressvik", 5.4, false);
        ferryRoutes.add(Vaerste_Gressvik);
        DistanceBetweenStops Vaerste_Aalekilen = new DistanceBetweenStops("Værste", "Ålekilen", 6.4, false);
        ferryRoutes.add(Vaerste_Aalekilen);

        // Fra Gressvik

        DistanceBetweenStops Gressvik_Gamlebyen = new DistanceBetweenStops("Gressvik", "Gamlebyen", 8.6, true);
        ferryRoutes.add(Gressvik_Gamlebyen);
        DistanceBetweenStops Gressvik_Smertu = new DistanceBetweenStops("Gressvik", "Smertu", 6.8, true);
        ferryRoutes.add(Gressvik_Smertu);
        DistanceBetweenStops Gressvik_Sentrum = new DistanceBetweenStops("Gressvik", "Sentrum", 5.8, true);
        ferryRoutes.add(Gressvik_Sentrum);
        DistanceBetweenStops Gressvik_Vaerste = new DistanceBetweenStops("Gressvik", "Værste", 5.4, true);
        ferryRoutes.add(Gressvik_Vaerste);
        DistanceBetweenStops Gressvik_Aalekilen = new DistanceBetweenStops("Gressvik", "Ålekilen", 1.8, false);
        ferryRoutes.add(Gressvik_Aalekilen);

        // Fra Ålekilen

        DistanceBetweenStops Aalekilen_Gamlebyen = new DistanceBetweenStops("Ålekilen", "Gamlebyen", 9.5, true);
        ferryRoutes.add(Aalekilen_Gamlebyen);
        DistanceBetweenStops Aalekilen_Smertu = new DistanceBetweenStops("Ålekilen", "Smertu", 7.2, true);
        ferryRoutes.add(Aalekilen_Smertu);
        DistanceBetweenStops Aalekilen_Sentrum = new DistanceBetweenStops("Ålekilen", "Sentrum", 6.8, true);
        ferryRoutes.add(Aalekilen_Sentrum);
        DistanceBetweenStops Aalekilen_Vaerste = new DistanceBetweenStops("Ålekilen", "Værste", 6.4, true);
        ferryRoutes.add(Aalekilen_Vaerste);
        DistanceBetweenStops Aalekilen_Gressvik = new DistanceBetweenStops("Ålekilen", "Gressvik", 1.8, false);
        ferryRoutes.add(Aalekilen_Gressvik);

        // Fra Cicignon

        DistanceBetweenStops Cicignon_Gamlebyen = new DistanceBetweenStops("Cicignon", "Gamlebyen", 3.6, false);
        ferryRoutes.add(Cicignon_Gamlebyen);

        // Fra Lisleby

        DistanceBetweenStops Lisleby_Sellebakk = new DistanceBetweenStops("Lisleby", "Sellebakk", 10.2, false);
        ferryRoutes.add(Lisleby_Sellebakk);


        // Fra Sellebakk

        DistanceBetweenStops Sellebakk_Lisleby = new DistanceBetweenStops("Sellebakk", "Lisleby", 10.2, true);
        ferryRoutes.add(Sellebakk_Lisleby);




        // Oppretter JSON-filen og kaller den ferryRoutes.json
        File jsonFile = new File("ferryRoutes.json");


        // Oppretter et repository-objekt som håndterer lesing og skriving til JSON-filen
        ferryRoutesJSONRepository ferryRoutesJSON= new ferryRoutesJSONRepository(jsonFile);

        // Skriver alle ferge rutene fra listen ferryRoutes til JSON-filen
        ferryRoutesJSON.addListOfFerryRoutes(ferryRoutes);


        // Henter alle fergerutene fra JSON-filen


        ferryRoutesJSON.getAllFerryRoutes();

        // For-løkke for å skrive ut alle rutene som er i JSON-filen i terminalen
        for (DistanceBetweenStops ferry : ferryRoutesJSON.getAllFerryRoutes()) {
            System.out.println("From: " + ferry.getFrom() + ", Destination: " + ferry.getDestination());
            System.out.println();
        }





    }



}
