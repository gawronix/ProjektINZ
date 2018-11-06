package wmi;

import com.google.gson.Gson;
import java.sql.SQLException;
import java.util.List;


public class rest {
String number;

rest(String a){
	this.number=a;
}
	
 public String getchannelDetails() throws SQLException {
	String productDetails = null;
 	List channelList = null;
 	
    channelsworks channel = new channelsworks(number);
    channelList = channel.getChannelDetails();
    
    
    Gson gson = new Gson();
   productDetails = gson.toJson(channelList);
   return productDetails;
 }

}

