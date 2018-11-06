package wmi;

import java.sql.Connection;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("")
public class JerseyRestService {

	@GET
	@Path("/top/{number}")		
	@Produces(MediaType.APPLICATION_JSON)
	public String produceJSON( @PathParam("number") String number ) throws SQLException {
		
		rest r = new rest(number);
        String s = r.getchannelDetails();
		return s;
	}
	
	@GET
	@Path("/search/{channel}")		
	@Produces(MediaType.APPLICATION_JSON)
	public String search( @PathParam("channel") String channel ) throws SQLException {
			
		search s = new search(channel);
		return s.channelDetails;	
	
	}
	

}
