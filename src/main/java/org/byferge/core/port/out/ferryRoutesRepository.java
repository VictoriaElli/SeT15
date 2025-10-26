package org.byferge.core.port.out;

import org.byferge.core.domain.model.environment.Route;

import java.util.ArrayList;

public interface ferryRoutesRepository {


    void addListOfFerryRoutes(ArrayList<Route> listOfFerryRoutes);

    ArrayList<Route> getAllFerryRoutes();


}
