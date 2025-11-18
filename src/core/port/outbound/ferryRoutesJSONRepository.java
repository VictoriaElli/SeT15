package port.outbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.model.environment.DistanceBetweenStops;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ferryRoutesJSONRepository implements ferryRoutesRepository {

    private File file;


    // Konstruktør som tar inn en fil som variabel og lagrer den i klassen
    // Metodekall vil kunne lese og skrive på denne spesifikke filen
    public ferryRoutesJSONRepository(File file) {
        this.file = file;
    }

    // Override fra interfacet
    @Override
    // Metode som tar imot fergerute objekter fra DistanceBetweenStops og legger de til i JSON filen
    public void addListOfFerryRoutes(ArrayList<DistanceBetweenStops> listOfFerryRoutes) {
        // ObjectMapper benyttes til konvertere mellom java objektene og JSON
        ObjectMapper objectMapper = new ObjectMapper();
        // Metode som registrerer moduler automatisk
        objectMapper.findAndRegisterModules();

        // Skriver om listen med objektene til en fint format til filen som er registrert
        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, listOfFerryRoutes);
        }
        // Hvis det skjer en feil med å skrive over objektene til fil, skriver den ut en feilmelding
        catch (IOException exception) {
            System.err.println(exception.getMessage());;
        }
    }

    // Override fra interfacet
    @Override
    // Metode som henter alle objektene fra en JSON fil og legger de til en liste
    public ArrayList<DistanceBetweenStops> getAllFerryRoutes() {
        // ObjectMapper benyttes til konvertere mellom java objektene og JSON
        ObjectMapper objectMapper = new ObjectMapper();
        // Metode som registrerer moduler automatisk
        objectMapper.findAndRegisterModules();

        // Leser av en oppgitt JSON fil
        try {
            DistanceBetweenStops[] ferryRoutesArray = objectMapper.readValue(file, DistanceBetweenStops[].class);

            // Returnerer dem som en arrayliste
            return new ArrayList<>(Arrays.asList(ferryRoutesArray));
        }

        // Skriver en feilmelding hvsi det skjer en feil når den leser JSON filen og skriver om til liste
        catch (IOException exception) {
            System.err.println(exception.getMessage());
        }

        // Hvis alt går bra returnerer den arraylisten
        return new ArrayList<>();

    }

}
