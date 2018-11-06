package pl.youstats;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static pl.youstats.Youtube.streamToString;

public class UpdateStatistics {

public static void fill() throws ClassNotFoundException, SQLException, JSONException, IOException {

    Class.forName("com.mysql.jdbc.Driver");
    Connection con = (Connection) DriverManager.getConnection(
            "jdbc:mysql://145.239.90.206:3306/youstats?useUnicode=true&characterEncoding=utf-8", "youstats",
            "wmiuam");
    PreparedStatement pst;

        System.out.println("Updating channels table");
        pst = (PreparedStatement) con.prepareStatement("SELECT c_id FROM channels");
        ResultSet rs = pst.executeQuery();
        int counter = 0;
        while (rs.next()) {
            List<Object> statisticsList = new ArrayList<>(Youtube.getChannelStatistics(rs.getString("c_id")));

            String query = "UPDATE channels "
                    + "SET total_subscribers = ?, total_views = ?, description = ?, creationDate = ?, country = ?, total_videos = ? "
                    + "WHERE c_id = ?";

            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setObject(1, statisticsList.get(4));
            pstmt.setObject(2, statisticsList.get(3));
            pstmt.setObject(3, statisticsList.get(0));
            pstmt.setObject(4, statisticsList.get(1));
            pstmt.setObject(5, statisticsList.get(2));
            pstmt.setObject(6, statisticsList.get(5));
            pstmt.setString(7, rs.getString("c_id"));

            int row = pstmt.executeUpdate();
            counter++;

        }

        System.out.println("Records updated in channels table: " + counter);
        System.out.println();

    con.close();
}


public static void updateCategories() throws ClassNotFoundException, SQLException, JSONException, IOException, InterruptedException {

    Class.forName("com.mysql.jdbc.Driver");
    Connection con = (Connection) DriverManager.getConnection(
            "jdbc:mysql://145.239.90.206:3306/youstats?useUnicode=true&characterEncoding=utf-8", "youstats",
            "wmiuam");
    PreparedStatement pst;

        System.out.println("Updating categories in channels table.");
        pst = (PreparedStatement) con.prepareStatement("SELECT c_id FROM channels WHERE category_id IS NULL ");
        ResultSet rs = pst.executeQuery();
        int counter = 0;
        while (rs.next()) {
                String channel_id = rs.getString("c_id");
                String channelCategory = Youtube.findCategoryOfChannelFromID(channel_id);

                int category_id = 0;
                if (channelCategory.equals("Comedy")) {
                    category_id = 1;
                } else if (channelCategory.equals("Music")) {
                    category_id = 2;
                } else if (channelCategory.equals("Sports")) {
                    category_id = 3;
                } else if (channelCategory.equals("Science & Technology")) {
                    category_id = 4;
                } else if (channelCategory.equals("Entertainment")) {
                    category_id = 5;
                } else if (channelCategory.equals("People & Blogs")) {
                    category_id = 6;
                } else if (channelCategory.equals("Howto & Style")) {
                    category_id = 7;
                } else if (channelCategory.equals("Education")) {
                    category_id = 8;
                } else if (channelCategory.equals("Gaming")) {
                    category_id = 9;
                } else if (channelCategory.equals("Trailers")) {
                    category_id = 10;
                } else if (channelCategory.equals("Film & Animation")) {
                    category_id = 11;
                } else if (channelCategory.equals("Pets & Animals")) {
                    category_id = 12;
                } else if (channelCategory.equals("Autos & Vehicles")) {
                    category_id = 13;
                } else {
                    category_id = 14;
                }

                String query = "UPDATE channels "
                        + "SET category_id = ? WHERE c_id = ?";

                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setInt(1, category_id);
                pstmt.setString(2, channel_id);

                int row = pstmt.executeUpdate();
                counter++;
                System.out.println(counter);
    }
    System.out.println("Records updated in channels table: " + counter);
    Logs.updateDatabaseLog("Number of categories updated: " + counter);
    System.out.println();
    con.close();
}


    public static void updateThumbnails() throws JSONException, IOException, ClassNotFoundException, SQLException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection con = (Connection) DriverManager.getConnection(
                "jdbc:mysql://145.239.90.206:3306/youstats?useUnicode=true&characterEncoding=utf-8", "youstats",
                "wmiuam");
        PreparedStatement pst;

        System.out.println("Updating thumbnails in channels table.");
        pst = (PreparedStatement) con.prepareStatement("SELECT c_id FROM channels WHERE thumbnail IS NULL ");
        ResultSet rs = pst.executeQuery();
        int counter = 0;
        while (rs.next()) {
            String channel_id = rs.getString("c_id");

            String urlAPI = "https://www.googleapis.com/youtube/v3/channels?part=snippet,statistics&id=" + channel_id + "&key=" + Settings.getApiKey() + "";

            String json = null;
            try {
                URL url = new URL(urlAPI);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setInstanceFollowRedirects(false);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("charset", "utf-8");
                connection.connect();
                InputStream inStream = connection.getInputStream();
                json = streamToString(inStream);  // wynik json w postaci stringa

                JSONObject jsonObject = new JSONObject();
                jsonObject = new JSONObject(json);
                JSONArray items = jsonObject.getJSONArray("items");
                JSONObject idObject = items.getJSONObject(0);

                String link = idObject.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url");
                String query = "UPDATE channels "+"SET thumbnail = ? WHERE c_id = ?";

                PreparedStatement pstmt = con.prepareStatement(query);
                pstmt.setString(1,link );
                pstmt.setString(2, channel_id);

                int row = pstmt.executeUpdate();
                counter++;

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        System.out.println("Updating thumbnails - done.");
        Logs.updateDatabaseLog("Number of updated thumbnails: " + counter);

        con.close();
    }



public static void deleteBySubscribers(String category) throws ClassNotFoundException, SQLException {

    Class.forName("com.mysql.jdbc.Driver");
    Connection con = (Connection) DriverManager.getConnection(
            "jdbc:mysql://145.239.90.206:3306/youstats?useUnicode=true&characterEncoding=utf-8", "youstats",
            "wmiuam");
    PreparedStatement st = con.prepareStatement("DELETE FROM "+category+" WHERE total_subscribers < ?");
    st.setInt(1, Settings.getMinSubsForCategory(category));
    int rows = st.executeUpdate();
    System.out.println("Deleted: " + rows + " rows.");
    con.close();
}

}
