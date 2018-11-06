package pl.youstats;

import java.sql.*;

public class Settings {


public static String apiKey = "AIzaSyCOSupGCh2TPgM6Gu0JR2RvN5l1GtziOxw";


public static String getApiKey()
{
    return apiKey;
}

public static int getMinSubsForCategory(String category) throws SQLException, ClassNotFoundException {
    int subscriptions = 0;

    Class.forName("com.mysql.jdbc.Driver");
    Connection con = (Connection) DriverManager.getConnection(
            "jdbc:mysql://145.239.90.206:3306/youstats?useUnicode=true&characterEncoding=utf-8", "youstats",
            "wmiuam");
    PreparedStatement pst;
    pst = (PreparedStatement) con.prepareStatement("SELECT subscription FROM subscriptionSettings WHERE category_name = '"+category+"'");
    ResultSet rs = pst.executeQuery();
    if (rs.next())
    {
        subscriptions = rs.getInt("subscription");
    }
    con.close();
    return subscriptions;
}






}
