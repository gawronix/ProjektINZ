package wmi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class channelsworks {
  
	String number;
		channelsworks(String namber)
		{
			this.number = namber;
		}
		
        public List getChannelDetails() throws SQLException{
        List channelsInfo = new ArrayList<>();//lista objektow 
        
        dbconnection jdbcConnection = new dbconnection();
        Connection connection = jdbcConnection.getConnnection(); //tworzymy polaczenie z baza" connection"
        try {
            PreparedStatement ps = connection.prepareStatement(
            "select title,c_id,total_subscribers,total_videos,total_views,country, category_id from channels order by total_views desc limit " + number);
             ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                channel ch = new channel();
                ch.setCountry(rs.getString("country"));
                ch.setCategory(rs.getString("category_id"));
                ch.setURL(rs.getString("c_id"));
                ch.setTitle(rs.getString("title"));
                ch.setViews(rs.getString("total_views"));
                ch.setVideos(rs.getString("total_videos"));
                ch.setSubscribers(rs.getString("total_subscribers"));
                channelsInfo.add(ch);
            }
               

            

            } catch (SQLException e) {
            // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return channelsInfo;

            
    
        }



    
}
