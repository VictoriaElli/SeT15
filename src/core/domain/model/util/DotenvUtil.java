package domain.model.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DotenvUtil {

    // Map som holder på miljøvariablene som blir hentet fra .env-filen
    private Map<String, String> envVariables;

    // Standard konstruktør som bruker standard filbane (.env i rotkatalogen)
    public DotenvUtil() throws IOException {
        this(".env"); // Bruk standard bane for .env-filen
    }

    // Konstruktør som lar brukeren spesifisere filbanen til .env-filen
    public DotenvUtil(String filePath) throws IOException {
        envVariables = new HashMap<>(); // Initialiserer en HashMap for å lagre nøkkel-verdi par
        load(filePath); // Kaller load-metoden for å lese .env-filen
    }

    // Metode for å laste inn variabler fra .env-filen
    private void load(String filePath) throws IOException {
        // Leser filen linje for linje
        Files.lines(Paths.get(filePath))
                // Filtrer ut tomme linjer og kommentarer (linjer som starter med '#')
                .filter(line -> line.trim().length() > 0 && !line.startsWith("#"))
                .forEach(line -> {
                    // Splitter linjen ved '=' for å hente nøkkel og verdi
                    String[] parts = line.split("=", 2);
                    if (parts.length == 2) {
                        // Legg nøkkel og verdi til i Map-en
                        envVariables.put(parts[0].trim(), parts[1].trim());
                    }
                });
    }

    // Metode for å hente en verdi for en spesifikk nøkkel fra miljøvariablene
    public String get(String key) {
        // Sjekker om nøkkelen finnes i map-en og returnerer verdien
        if (envVariables.containsKey(key)) {
            return envVariables.get(key);
        }
        // Returnerer null hvis nøkkelen ikke finnes i map-en
        return null;
    }

    // Metoder for å hente spesifikke miljøvariabler relatert til databasen
    public String getDatabaseUrl() {
        return get("DB_URL"); // Henter verdien for DB_URL
    }

    public String getDatabaseUsername() {
        return get("DB_USER"); // Henter verdien for DB_USER
    }

    public String getDatabasePassword() {
        return get("DB_PASSWORD"); // Henter verdien for DB_PASSWORD
    }
}
