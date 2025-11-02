package core.domain.model.environment;

import core.adapter.ferryRoutesJSONRepository;
import org.byferge.core.domain.model.Route;


import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DistanceBetweenStopsObjects {

    public static void main(String[] args) {


        // Arrayliste som skal holde på alle objektene av DistanceBetweenStops. Altså alle ferge rutene
        ArrayList<org.byferge.core.domain.model.environment.DistanceBetweenStops> ferryRoutes = new ArrayList<>();


        // Fra Gamlebyen

        DistanceBetweenStops Gamlebyen_Cicignon = new DistanceBetweenStops("Gamlebyen", "Cicignon", 3.6, true);
        DistanceBetweenStops Gamlebyen_Smertu = new DistanceBetweenStops("Gamlebyen", "Smertu", 4.3, true);
        DistanceBetweenStops Gamlebyen_Sentrum = new DistanceBetweenStops("Gamlebyen", "Sentrum", 4.3, true);
        DistanceBetweenStops Gamlebyen_Vaerste = new DistanceBetweenStops("Gamlebyen", "Værste", 5.1, true);
        DistanceBetweenStops Gamlebyen_Gressvik = new DistanceBetweenStops("Gamlebyen", "Gressvik", 8.6, true);
        DistanceBetweenStops Gamlebyen_Aalekilen = new DistanceBetweenStops("Gamlebyen", "Ålekilen", 9.5, true);

        // Fra Smertu

        DistanceBetweenStops Smertu_Gamlebyen = new DistanceBetweenStops("Smertu", "Gamlebyen", 4.3, false);
        DistanceBetweenStops Smertu_Sentrum = new DistanceBetweenStops("Smertu", "Sentrum", 2.4, false);
        DistanceBetweenStops Smertu_Vaerste= new DistanceBetweenStops("Smertu", "Værste", 1.9, false);
        DistanceBetweenStops Smertu_Gressvik= new DistanceBetweenStops("Smertu", "Gressvik", 6.8, false);
        DistanceBetweenStops Smertu_Aalekilen= new DistanceBetweenStops("Smertu", "Ålekilen", 7.2, false);

        // Fra Sentrum

        DistanceBetweenStops Sentrum_Gamlebyen = new DistanceBetweenStops("Sentrum", "Gamlebyen", 4.3, false);
        DistanceBetweenStops Sentrum_Smertu = new DistanceBetweenStops("Sentrum", "Smertu", 2.4, false);
        DistanceBetweenStops Sentrum_Vaerste = new DistanceBetweenStops("Sentrum", "Værste", 2.3, false);
        DistanceBetweenStops Sentrum_Gressvik = new DistanceBetweenStops("Sentrum", "Gressvik", 5.8, false);
        DistanceBetweenStops Sentrum_Aalekilen = new DistanceBetweenStops("Sentrum", "Ålekilen", 6.8, false);

        // Fra Værste

        DistanceBetweenStops Vaerste_Gamlebyen = new DistanceBetweenStops("Værste", "Gamlebyen", 5.1, false);
        DistanceBetweenStops Vaerste_Smertu = new DistanceBetweenStops("Værste", "Smertu", 1.9, false);
        DistanceBetweenStops Vaerste_Sentrum = new DistanceBetweenStops("Værste", "Sentrum", 2.3, false);
        DistanceBetweenStops Vaerste_Gressvik = new DistanceBetweenStops("Værste", "Gressvik", 5.4, false);
        DistanceBetweenStops Vaerste_Aalekilen = new DistanceBetweenStops("Værste", "Ålekilen", 6.4, false);

        // Fra Gressvik

        DistanceBetweenStops Gressvik_Gamlebyen = new DistanceBetweenStops("Gressvik", "Gamlebyen", 8.6, true);
        DistanceBetweenStops Gressvik_Smertu = new DistanceBetweenStops("Gressvik", "Smertu", 6.8, true);
        DistanceBetweenStops Gressvik_Sentrum = new DistanceBetweenStops("Gressvik", "Sentrum", 5.8, true);
        DistanceBetweenStops Gressvik_Vaerste = new DistanceBetweenStops("Gressvik", "Værste", 5.4, true);
        DistanceBetweenStops Gressvik_Aalekilen = new DistanceBetweenStops("Gressvik", "Ålekilen", 1.8, false);

        // Fra Ålekilen

        DistanceBetweenStops Aalekilen_Gamlebyen = new DistanceBetweenStops("Ålekilen", "Gamlebyen", 9.5, true);
        DistanceBetweenStops Aalekilen_Smertu = new DistanceBetweenStops("Ålekilen", "Smertu", 7.2, true);
        DistanceBetweenStops Aalekilen_Sentrum = new DistanceBetweenStops("Ålekilen", "Sentrum", 6.8, true);
        DistanceBetweenStops Aalekilen_Vaerste = new DistanceBetweenStops("Ålekilen", "Værste", 6.4, true);
        DistanceBetweenStops Aalekilen_Gressvik = new DistanceBetweenStops("Ålekilen", "Gressvik", 1.8, false);

        // Fra Cicignon

        DistanceBetweenStops Cicignon_Gamlebyen = new DistanceBetweenStops("Cicignon", "Gamlebyen", 3.6, false);

        // Fra Lisleby

        DistanceBetweenStops Lisleby_Sellebakk = new DistanceBetweenStops("Lisleby", "Sellebakk", 10.2, false);


        // Fra Sellebakk

        DistanceBetweenStops Sellebakk_Lisleby = new DistanceBetweenStops("Sellebakk", "Lisleby", 10.2, true);



        // Legger til rutene i ferryRoutes arraylisten
        ferryRoutes.add(Gamlebyen_Cicignon);
        ferryRoutes.add(Gamlebyen_Smertu);


        // Oppretter JSON-filen og kaller den ferryRoutes.json
        File jsonFile = new File("ferryRoutes.json");


        // Oppretter et repository-objekt som håndterer lesing og skriving til JSON-filen
        ferryRoutesJSONRepository ferryRoutesJSON= new ferryRoutesJSONRepository(jsonFile);

        // Skriver alle ferge rutene fra listen ferryRoutes til JSON-filen
        ferryRoutesJSON.addListOfFerryRoutes(ferryRoutes);


        // Henter alle fergerutene fra JSON-filen
        ferryRoutesJSON.getAllFerryRoutes();

        // For-løkke for å skrive ut alle rutene som er i JSON-filen
        for (DistanceBetweenStops ferry : ferryRoutesJSON.getAllFerryRoutes()) {
            System.out.println("From: " + ferry.getFrom() + ", Destination: " + ferry.getDestination());
            System.out.println();
        }


    }



}
