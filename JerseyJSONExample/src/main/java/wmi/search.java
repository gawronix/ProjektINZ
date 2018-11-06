package wmi;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.google.gson.Gson;

public class search {

	String channelDetails = null;
 	channel ch = new channel();
	String name;
	
	    			    
	
	search(String name) throws SQLException{
		this.name = name;	
		
		dbconnection jdbcConnection = new dbconnection();
    	Connection connection = jdbcConnection.getConnnection(); //tworzymy polaczenie z baza" connection"
    		try {
 
    				PreparedStatement ps = connection.prepareStatement(
    						"select title,c_id,total_subscribers,total_videos,total_views,country,category_id from channels where title ='" + name+"'");
    				ResultSet rs = ps.executeQuery();

    				   while (rs.next()) {
    					this.ch.setCountry(rs.getString("country"));
    					this.ch.setCategory(rs.getString("category_id"));
    					this.ch.setURL(rs.getString("c_id"));
    					this.ch.setTitle(rs.getString("title"));
    					this.ch.setViews(rs.getString("total_views"));
    					this.ch.setVideos(rs.getString("total_videos"));
    					this.ch.setSubscribers(rs.getString("total_subscribers"));
    				   }
    			} catch (SQLException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
		
		Gson gson = new Gson();
	    this.channelDetails = gson.toJson(ch);
	    //System.out.println(channelDetails);
	}
	
 
    
    
}
