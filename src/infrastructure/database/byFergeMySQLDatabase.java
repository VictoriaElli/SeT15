package org.byferge.infrastructure.database;


// Tilknyttning til databasen

import java.sql.Connection;

public class byFergeMySQLDatabase {
    private String url;
    private String username;
    private String password;
    private Connection connection;


    public byFergeMySQLDatabase(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }


}
