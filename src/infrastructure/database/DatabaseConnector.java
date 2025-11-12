package database;

import domain.model.util.DotenvUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.IOException;

public class DatabaseConnector {

    public static Connection connect() throws SQLException, IOException {
        // Opprett Dotenv-objekt og last inn .env-filen
        DotenvUtil dotenv = new DotenvUtil(".env");

        // Hent nødvendige konfigurasjoner fra miljøvariablene
        String dbHost = dotenv.get("DB_HOST");
        String dbPort = dotenv.get("DB_PORT");
        String dbName = dotenv.get("DB_NAME");
        String dbUsername = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");

        // Dynamisk bygg DB_URL
        String dbUrl = String.format("jdbc:mysql://%s:%s/%s", dbHost, dbPort, dbName);

        // Opprett og returner databaseforbindelsen
        return DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
    }

    public static void main(String[] args) {
        try (Connection connection = connect()) {
            if (connection != null) {
                System.out.println("Database connection successful!");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
