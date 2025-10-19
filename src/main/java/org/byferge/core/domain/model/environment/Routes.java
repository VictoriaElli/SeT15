package org.byferge.core.domain.model.environment;

public class Routes {

    // Fra Gamlebyen

    Route Gamlebyen_Cicignon = new Route("Gamlebyen", "Cicignon", 3.6, true);
    Route Gamlebyen_Smertu = new Route("Gamlebyen", "Smertu", 4.3, true);
    Route Gamlebyen_Sentrum = new Route("Gamlebyen", "Sentrum", 4.3, true);
    Route Gamlebyen_Vaerste = new Route("Gamlebyen", "Værste", 5.1, true);
    Route Gamlebyen_Gressvik = new Route("Gamlebyen", "Gressvik", 8.6, true);
    Route Gamlebyen_Aalekilen = new Route("Gamlebyen", "Ålekilen", 9.5, true);

    // Fra Smertu

    Route Smertu_Gamlebyen = new Route("Smertu", "Gamlebyen", 4.3, false);
    Route Smertu_Sentrum = new Route("Smertu", "Sentrum", 2.4, false);
    Route Smertu_Vaerste= new Route("Smertu", "Værste", 1.9, false);
    Route Smertu_Gressvik= new Route("Smertu", "Gressvik", 6.8, false);
    Route Smertu_Aalekilen= new Route("Smertu", "Ålekilen", 7.2, false);

    // Fra Sentrum

    Route Sentrum_Gamlebyen = new Route("Sentrum", "Gamlebyen", 4.3, false);
    Route Sentrum_Smertu = new Route("Sentrum", "Smertu", 2.4, false);
    Route Sentrum_Vaerste = new Route("Sentrum", "Værste", 2.3, false);
    Route Sentrum_Gressvik = new Route("Sentrum", "Gressvik", 5.8, false);
    Route Sentrum_Aalekilen = new Route("Sentrum", "Ålekilen", 6.8, false);

    // Fra Værste

    Route Vaerste_Gamlebyen = new Route("Værste", "Gamlebyen", 5.1, false);
    Route Vaerste_Smertu = new Route("Værste", "Smertu", 1.9, false);
    Route Vaerste_Sentrum = new Route("Værste", "Sentrum", 2.3, false);
    Route Vaerste_Gressvik = new Route("Værste", "Gressvik", 5.4, false);
    Route Vaerste_Aalekilen = new Route("Værste", "Ålekilen", 6.4, false);

    // Fra Gressvik

    Route Gressvik_Gamlebyen = new Route("Gressvik", "Gamlebyen", 8.6, true);
    Route Gressvik_Smertu = new Route("Gressvik", "Smertu", 6.8, true);
    Route Gressvik_Sentrum = new Route("Gressvik", "Sentrum", 5.8, true);
    Route Gressvik_Vaerste = new Route("Gressvik", "Værste", 5.4, true);
    Route Gressvik_Aalekilen = new Route("Gressvik", "Ålekilen", 1.8, false);

    // Fra Ålekilen

    Route Aalekilen_Gamlebyen = new Route("Ålekilen", "Gamlebyen", 9.5, true);
    Route Aalekilen_Smertu = new Route("Ålekilen", "Smertu", 7.2, true);
    Route Aalekilen_Sentrum = new Route("Ålekilen", "Sentrum", 6.8, true);
    Route Aalekilen_Vaerste = new Route("Ålekilen", "Værste", 6.4, true);
    Route Aalekilen_Gressvik = new Route("Ålekilen", "Gressvik", 1.8, false);

    // Fra Cicignon

    Route Cicignon_Gamlebyen = new Route("Cicignon", "Gamlebyen", 3.6, false);

    // Fra Lisleby

    Route Lisleby_Sellebakk = new Route("Lisleby", "Sellebakk", 10.2, false);


    // Fra Sellebakk

    Route Sellebakk_Lisleby = new Route("Sellebakk", "Lisleby", 10.2, true);



}
