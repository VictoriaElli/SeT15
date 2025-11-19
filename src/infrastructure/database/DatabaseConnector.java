package database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import exception.MySQLDatabaseException;
import io.github.cdimascio.dotenv.Dotenv;

import javax.sql.DataSource;

public class DatabaseConnector {

    private static HikariDataSource dataSource;

    public static void init() {
        if (dataSource != null) return; // allerede initialisert

        // Leser .env
        Dotenv dotenv = Dotenv.configure()
                .directory("src/resources")
                .filename("database.env")
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        String dbHost = dotenv.get("DB_HOST");
        String dbPort = dotenv.get("DB_PORT");
        String dbName = dotenv.get("DB_NAME");
        String dbUser = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");

        if (dbHost == null || dbPort == null || dbName == null || dbUser == null || dbPassword == null) {
            throw new MySQLDatabaseException("Database credentials missing in .env", null);
        }

        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s?useSSL=false&serverTimezone=UTC",
                dbHost, dbPort, dbName);

        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(dbUser);
            config.setPassword(dbPassword);

            dataSource = new HikariDataSource(config);
        } catch (Exception e) {
            throw new MySQLDatabaseException("Failed to initialize HikariCP DataSource", e);
        }
    }

    public static DataSource getDataSource() {
        if (dataSource == null) init();
        return dataSource;
    }
}
