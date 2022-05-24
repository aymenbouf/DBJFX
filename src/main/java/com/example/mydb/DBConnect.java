package com.example.mydb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    String hostname,username,port,SID,password;

    public DBConnect(String hostname, String username, String port, String SID, String password) {
        this.hostname = hostname;
        this.username = username;
        this.port = port;
        this.SID = SID;
        this.password = password;
    }

    public Connection Connect() throws SQLException {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            return DriverManager.getConnection(
                    "jdbc:oracle:thin:@"+ this.hostname+":"+ this.port+":"+ this.SID, this.username, this.password);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

}
