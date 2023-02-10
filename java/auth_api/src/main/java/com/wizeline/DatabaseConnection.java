package com.wizeline;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection databaseConnection;
    private String url = "jdbc:mysql://sre-bootcamp-selection-challenge.cabf3yhjqvmq.us-east-1.rds.amazonaws.com/bootcamp_tht";
    private String userName = "secret";
    private String pwd = "jOdznoyH6swQB9sTGdLUeeSrtejWkcw";

    private Connection connection;

    private DatabaseConnection() throws SQLException {
        this.connection = DriverManager.getConnection(url, userName,pwd);
    }

    public Connection getConnection(){
        return this.connection;
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if(databaseConnection == null){
            databaseConnection = new DatabaseConnection();
        } else if (databaseConnection.getConnection().isClosed()) {
            databaseConnection = new DatabaseConnection();
        }
        return databaseConnection;
    }


}
