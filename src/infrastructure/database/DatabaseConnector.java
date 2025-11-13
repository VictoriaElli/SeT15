package database;

import domain.model.util.DotenvUtil;
import exception.MySQLDatabaseException;
import java.sql.Connection;
import java.sql.SQLException;
import java.io.IOException;

public class DatabaseConnector {

    // Metode for å etablere en tilkobling til databasen ved å bruke MySQLDatabase
    public static Connection connect() throws SQLException, IOException, MySQLDatabaseException {
        // Opprett Dotenv-objekt og last inn .env-filen for å hente miljøvariabler
        DotenvUtil dotenv = new DotenvUtil("database.env");

        // Hent nødvendige konfigurasjoner fra miljøvariablene (fra .env-filen)
        String dbHost = dotenv.get("DB_HOST");
        String dbPort = dotenv.get("DB_PORT");
        String dbName = dotenv.get("DB_NAME");
        String dbUsername = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");

        // Logg miljøvariabler for debugging (huske å fjerne i produksjon for å unngå lekkasje av sensitive data)
        System.out.println("Connecting to DB:");
        System.out.println("Host: " + dbHost);
        System.out.println("Port: " + dbPort);
        System.out.println("Database: " + dbName);
        System.out.println("Username: " + dbUsername);

        // Bygg database-URL dynamisk basert på de hentede miljøvariablene
        String dbUrl = String.format("jdbc:mysql://%s:%s/%s", dbHost, dbPort, dbName);

        // Opprett MySQLDatabase-objekt og start tilkoblingen
        MySQLDatabase mysqlDatabase = new MySQLDatabase(dbUrl, dbUsername, dbPassword);
        return mysqlDatabase.startDB();  // Kall startDB() for å opprette og returnere en tilkobling
    }

    // Main-metode for å teste tilkoblingen
    public static void main(String[] args) {
        // Bruk try-with-resources for automatisk å lukke tilkoblingen etter bruk
        try (Connection connection = connect()) {
            if (connection != null) {
                // Hvis tilkoblingen er vellykket, vis melding i konsollen
                System.out.println("Database connection successful!");
            }
        } catch (SQLException | IOException | MySQLDatabaseException e) {
            e.printStackTrace();  // Skriv ut feil dersom tilkoblingen mislykkes
        }
    }
}
