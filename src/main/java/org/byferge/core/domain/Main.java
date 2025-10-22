package org.byferge.core.domain;

import org.byferge.core.domain.model.environment.Functions;
import org.byferge.core.domain.model.environment.Route;
import org.byferge.core.domain.model.environment.Routes;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {


        // Fra Gamlebyen

        Route Gamlebyen_Cicignon = new Route("Gamlebyen", "Cicignon", 3.6, true);


        Functions.calculateWhatYouHaveSaved(Gamlebyen_Cicignon);



    }
}
// Ekstra kake til alle 