package database;

import domain.model.util.DotenvUtil;
import exception.MySQLDatabaseException;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;

public class DatabaseConnector {

    // Metode for å etablere en tilkobling
    public static Connection connect() throws SQLException, IOException, MySQLDatabaseException {
        DotenvUtil dotenv = new DotenvUtil("src/resources/database.env");

        String dbHost = dotenv.get("DB_HOST");
        String dbPort = dotenv.get("DB_PORT");
        String dbName = dotenv.get("DB_NAME");
        String dbUsername = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");

        System.out.println("Connecting to DB:");
        System.out.println("Host: " + dbHost);
        System.out.println("Port: " + dbPort);
        System.out.println("Database: " + dbName);
        System.out.println("Username: " + dbUsername);

        String dbUrl = String.format("jdbc:mysql://%s:%s/%s", dbHost, dbPort, dbName);

        MySQLDatabase mysqlDatabase = new MySQLDatabase(dbUrl, dbUsername, dbPassword);
        return mysqlDatabase.startDB();
    }

    // Metode for å lese data fra databasen
    public static void readData(String query) {
        try (Connection connection = connect();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String column1 = rs.getString("id");
                String column2 = rs.getString("name");
                System.out.println(column1 + " | " + column2);
            }

        } catch (SQLException | IOException | MySQLDatabaseException e) {
            e.printStackTrace();
        }
    }

    // Main-metode for å teste tilkobling og lese data
    public static void main(String[] args) {
        try (Connection connection = connect()) {
            if (connection != null) {
                System.out.println("Database connection successful!");
            }
        } catch (SQLException | IOException | MySQLDatabaseException e) {
            e.printStackTrace();
        }

        // Les fra databasen
        readData("SELECT * FROM stops"); // Bytt 'route' med ønsket tabell


    }
}
