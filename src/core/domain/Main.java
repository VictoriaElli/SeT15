package domain;

import core.domain.model.environment.EnvironmentCalculator;
import core.domain.model.environment.DistanceBetweenStops;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {


        // Fra Gamlebyen

        DistanceBetweenStops Gamlebyen_Cicignon = new DistanceBetweenStops("Gamlebyen", "Cicignon", 3.6, true);

        EnvironmentCalculator.calculateWhatYouHaveSaved(Gamlebyen_Cicignon);


    }
}
// Ekstra kake til alle 