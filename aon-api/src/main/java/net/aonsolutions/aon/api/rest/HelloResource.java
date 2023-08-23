package net.aonsolutions.aon.api.rest;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class HelloResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{name}")
	public String sayHello(@PathParam("name") String name) {
		return "Hello " + name;	
	}
	
}
