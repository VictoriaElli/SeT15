package database;

import domain.model.util.DotenvUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;

public class DatabaseConnector {

    public static Connection connect() throws SQLException, IOException {
        // Opprett Dotenv-objekt og last inn .env-filen
        DotenvUtil dotenv = new DotenvUtil("database.env");

        // Hent nødvendige konfigurasjoner fra miljøvariablene
        String dbHost = dotenv.get("DB_HOST");
        String dbPort = dotenv.get("DB_PORT");
        String dbName = dotenv.get("DB_NAME");
        String dbUsername = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");

        // Logg miljøvariabler for debugging (skru av logging i produksjon for sensitiv informasjon)
        System.out.println("Connecting to DB:");
        System.out.println("Host: " + dbHost);
        System.out.println("Port: " + dbPort);
        System.out.println("Database: " + dbName);
        System.out.println("Username: " + dbUsername);

        // Dynamisk bygg DB_URL
        String dbUrl = String.format("jdbc:mysql://%s:%s/%s", dbHost, dbPort, dbName);

        // Forsøk å opprette og returner databaseforbindelsen
        try {
            Connection connection = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
            System.out.println("Database connection successful!");
            return connection;
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
            throw e;  // Kaster videre for at testene skal kunne fange opp feilen
        }
    }

    public static void main(String[] args) {
        try (Connection connection = connect()) {
            if (connection != null) {
                // Vist hvis tilkoblingen er vellykket
                System.out.println("Database connection successful!");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
