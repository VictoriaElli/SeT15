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
}
