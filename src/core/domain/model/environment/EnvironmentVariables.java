package domain.model.environment;

public class EnvironmentVariables {


    // Standard sats per kilometer i kr

    public final static double standardRatePrKm = 3.5;

    // Kostnad for ferge i kr

    public final static double ferryRate = 0;

    // Antall prosent med elbil eller drivstoff bil

    public final static double percentageElectric = 0.31;
    public final static double percentageGasoline = 0.29;
    public final static double percentageDiesel = 0.4;
    public final static double percentageFuel = percentageDiesel + percentageGasoline;

    // Kostnad for elbil eller drivstoffbil uten bombrikke i kr

    public final static double fuelWithoutChip = 34;
    public final static double electricWithoutChip = 34;

    // Kostnad for elbil eller drivstoffbil med bombrikke i kr
    public final static double fuelWithChip = 27.2;
    public final static double electricWithChip = 13.6;

    // Antall prosent som passerer bom med eller uten bombrikke
    public final static double passWithChip = 0.91;
    public final static double passWithoutChip = 0.09;

    // Utslipp CO2 per kilometer i gram for elbil, bensinbil eller dieselbil

    public final static double emissionElectric = 0;
    public final static double emissionGasoline = 151;
    public final static double emissionDiesel = 140;


    //  Formel for pris for gjennomsnittlig passering med bombrikke i kr

    public final static double averageCostThruTollgateWithChip = (percentageElectric * electricWithChip) + (percentageFuel * fuelWithChip);

    //  Formel for pris for gjennomsnittlig passering uten bombrikke i kr

    public final static double averageCostThruTollgateWithoutChip = (percentageElectric * electricWithoutChip) + (percentageFuel * fuelWithoutChip);

    // Formel for pris for en gjennomsnittlig passering gjennom en bom i kr

    public static double averageCostThruTollgate = (averageCostThruTollgateWithChip * passWithChip) + (averageCostThruTollgateWithoutChip * passWithoutChip);

    // Formel for gjennomsnitlig CO2 utslipp er km i gram

    public final static double averageEmissionPrKm = (emissionDiesel * percentageDiesel) + (emissionGasoline * percentageGasoline) + (emissionElectric * percentageElectric);





}
