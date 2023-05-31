package net.aonsolutions.api.rest;

import java.net.URI;
import java.util.Optional;

import org.json.JSONObject;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.core.UriInfo;
import net.aonsolutions.occam.api.AON;
import net.aonsolutions.occam.api.AonError;
import net.aonsolutions.occam.api.Occam;
import net.aonsolutions.occam.api.config.Domain;
import net.aonsolutions.occam.json.DomainJSON;

@Path("/")
public class DomainsRest {
	
	@Context UriInfo info;
	private static final String USER 		= "admin";
	
	@GET
	@Path("domains/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response hello() {
		URI requestURI = info.getRequestUri();
		String domainName = requestURI.getHost();
		Optional<Domain> optDomain = AON.getDomains(
	        	 new Occam().setDomainName(domainName).setUser(USER)
	    		, f -> f.withId().eq(85)
	    		, b -> b.withUsers()
	    			.withAudit()
	    			.withBooking()
	    			.withCompany()
	    			.withParentDomain()
	    			.withConfiguration( a -> a.withAccountingConfiguration())
	    			.limit(0, 3)
			)
	        .findFirst();
		if (optDomain.isPresent()) {
			return Response.ok( DomainJSON.to(optDomain.get()).toString() )
				.type(MediaType.APPLICATION_JSON)
				.build();
		}
		
		return Response.status( Status.OK ) 
            .entity(new JSONObject()
        		.put("status","200")
        		.put("message", AonError.DOMAIN_NOT_FOUND.getMessage())
        		.toString()
        		)
            .type(MediaType.APPLICATION_JSON)
            .build();		
	}
	
}