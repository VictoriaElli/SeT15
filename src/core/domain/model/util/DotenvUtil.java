package domain.model.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DotenvUtil {

    private Map<String, String> envVariables;

    // Standard filbane kan være .env i rotkatalogen
    public DotenvUtil() throws IOException {
        this(".env"); // Bruk standard bane for .env-filen
    }

    // Konstruktør med valgfri filbane
    public DotenvUtil(String filePath) throws IOException {
        envVariables = new HashMap<>();
        load(filePath);
    }

    // Laste inn .env-filen
    private void load(String filePath) throws IOException {
        Files.lines(Paths.get(filePath)) // Les filen linje for linje
                .filter(line -> line.trim().length() > 0 && !line.startsWith("#")) // Filtrer ut tomme linjer og kommentarer
                .forEach(line -> {
                    String[] parts = line.split("=", 2); // Del linjen på første '='
                    if (parts.length == 2) {
                        envVariables.put(parts[0].trim(), parts[1].trim()); // Legg til variabelen i map
                    }
                });
    }

    // Hente en verdi fra .env-fil
    public String get(String key) {
        if (envVariables.containsKey(key)) {
            return envVariables.get(key); // Returner verdien for nøkkelen
        }
        return null; // Returner null hvis nøkkelen ikke finnes
    }

    // For å gjøre det enklere å hente spesifikke miljøvariabler
    public String getDatabaseUrl() {
        return get("DB_URL");
    }

    public String getDatabaseUsername() {
        return get("DB_USER");
    }

    public String getDatabasePassword() {
        return get("DB_PASSWORD");
    }
}
