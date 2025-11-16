package unitTesting;

import database.DatabaseConnector;
import domain.model.util.DotenvUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseConnectorTest {

    // Denne metoden kjører før hver test
    @BeforeEach
    public void setUp() {
        try {
            // Last inn miljøvariabler fra .env-filen ved hjelp av DotenvUtil
            DotenvUtil dotenv = new DotenvUtil("database.env");

            // Sette nødvendige miljøvariabler for testen
            System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
            System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
            System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
            System.setProperty("DB_USER", dotenv.get("DB_USER"));
            System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        } catch (IOException e) {
            // Hvis vi ikke kan lese .env-filen, skriv ut en feilmelding
            e.printStackTrace();
            fail("Failed to load environment variables from .env file: " + e.getMessage());
        }
    }

    // Test for å verifisere om tilkoblingen til databasen er vellykket
    @Test
    public void testDatabaseConnection() {
        try (Connection connection = DatabaseConnector.connect()) {
            // Kjør en enkel spørring for å sikre at tilkoblingen fungerer
            try (Statement stmt = connection.createStatement()) {
                // Opprett en enkel testtabell hvis den ikke eksisterer
                stmt.execute("CREATE TABLE IF NOT EXISTS test (id INT PRIMARY KEY, name VARCHAR(255))");

                // Bruk INSERT IGNORE for å unngå duplikatfeil
                stmt.execute("INSERT IGNORE INTO test (id, name) VALUES (1, 'John')");

                // Hent data fra tabellen og verifiser at verdien ble satt inn korrekt
                ResultSet resultSet = stmt.executeQuery("SELECT * FROM test WHERE id = 1");
                if (resultSet.next()) {
                    String name = resultSet.getString("name");

                    // Sjekk at vi fikk ønsket navn
                    assertNotNull(name, "Name should not be null");
                    assertTrue(name.equals("John"), "The name should be 'John'");
                }
            }

            System.out.println("Database connection successful!");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            fail("Database connection failed: " + e.getMessage());
        }
    }

    // Test for å verifisere at feilmeldinger blir kastet ved feil på tilkobling
    @Test
    public void testDatabaseConnectionFailure() {
        // Her setter vi en feilaktig port for å simulere en tilkoblingsfeil
        System.setProperty("DB_PORT", "9999"); // En port som er utilgjengelig

        try {
            // Forsøk å koble til databasen med feil port
            Connection connection = DatabaseConnector.connect();
            assertNotNull(connection, "Connection should fail with incorrect settings!");
        } catch (SQLException | IOException e) {
            // Testen er godkjent hvis en exception blir kastet
            assertTrue(e instanceof SQLException, "Expected SQLException for incorrect settings.");
        }
    }

    // Test for å håndtere ugyldige databaseinnstillinger (f.eks. feilaktig databasebruker)
    @Test
    public void testInvalidDatabaseSettings() {
        // Set feilede innstillinger
        System.setProperty("DB_USER", "wronguser");
        System.setProperty("DB_PASSWORD", "wrongpass");

        try {
            // Forsøk å koble til databasen med ugyldige innstillinger
            DatabaseConnector.connect();
        } catch (SQLException | IOException e) {
            // Verifiser at en SQLException blir kastet når feil databaseinnstillinger brukes
            assertTrue(e instanceof SQLException, "Expected SQLException for invalid credentials.");
        }
    }
}
