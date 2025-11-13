package database;

import domain.model.util.DotenvUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;

public class DatabaseConnector {

    // Metode for å etablere en tilkobling til databasen
    public static Connection connect() throws SQLException, IOException {
        // Opprett Dotenv-objekt og last inn .env-filen for å hente miljøvariabler
        DotenvUtil dotenv = new DotenvUtil("database.env");

        // Hent nødvendige konfigurasjoner fra miljøvariablene (fra .env-filen)
        String dbHost = dotenv.get("DB_HOST"); // Hent verten for databasen
        String dbPort = dotenv.get("DB_PORT"); // Hent porten for databasen
        String dbName = dotenv.get("DB_NAME"); // Hent databasenavnet
        String dbUsername = dotenv.get("DB_USER"); // Hent brukernavnet for databasen
        String dbPassword = dotenv.get("DB_PASSWORD"); // Hent passordet for databasen

        // Logg miljøvariabler for debugging. Husk å fjerne logging i produksjon for å unngå lekkasje av sensitiv informasjon
        System.out.println("Connecting to DB:");
        System.out.println("Host: " + dbHost);
        System.out.println("Port: " + dbPort);
        System.out.println("Database: " + dbName);
        System.out.println("Username: " + dbUsername);

        // Bygg database-URL dynamisk basert på de hentede miljøvariablene
        String dbUrl = String.format("jdbc:mysql://%s:%s/%s", dbHost, dbPort, dbName);

        // Forsøk å opprette en tilkobling til databasen og returner forbindelsen
        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            System.out.println("Database connection successful!"); // Bekreft at tilkoblingen er vellykket
            return connection;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage()); // Feilhåndtering ved tilkoblingsfeil
            throw e;  // Kast unntaket videre slik at det kan fanges opp i testene
        }
    }

    // Main-metode for å teste tilkoblingen
    public static void main(String[] args) {
        // Bruk try-with-resources for automatisk å lukke tilkoblingen etter bruk
        try (Connection connection = connect()) {
            if (connection != null) {
                // Hvis tilkoblingen er vellykket, vis melding i konsollen
                System.out.println("Database connection successful!");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace(); // Skriv ut feil dersom tilkoblingen mislykkes
        }
    }
}
