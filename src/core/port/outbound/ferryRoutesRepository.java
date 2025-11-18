package port.outbound;

import domain.model.environment.DistanceBetweenStops;


import java.util.ArrayList;

public interface ferryRoutesRepository {

    // Standarisert metode i interfacet som skal hente alle fergerutene i en liste
    // Denne metoden skal deretter skrive alle fergerute objektene om til en fil
    void addListOfFerryRoutes(ArrayList<DistanceBetweenStops> listOfFerryRoutes);


    // Standarisert metode i interfacet som skal lese alle fergerutene i en fil
    // Denne metoden skal deretter lagge til alle fergerute objektene i en arrayliste
    ArrayList<DistanceBetweenStops> getAllFerryRoutes();


}
