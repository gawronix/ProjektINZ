package wmi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;



public class dbconnection {


    public Connection getConnnection() {
        Connection connection = null;

        try {
            String connectionURL = "jdbc:mysql://145.239.90.206:3306/youstats?useUnicode=true&characterEncoding=utf-8";
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            connection = DriverManager.getConnection(connectionURL, "youstats", "wmiuam");


        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
            // TODO Auto-generated catch block
            e.getLocalizedMessage();
        }
        return connection;
    }

}