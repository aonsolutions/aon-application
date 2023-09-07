package net.aonsolutions.aon.api.rest;

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

@Path("/examples")
public class ExamplesResource extends BasicResource{

	@Context UriInfo info;
	
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExamples() {
		return response(new JSONArray());	
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getExample(@PathParam("id") String id) {
		return response(new JSONObject());	
	}
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response postExample(Example example) {
		System.out.println(example);
		System.out.println(example.getId());
		System.out.println(example.getName());
		return response(new JSONObject());	
	}
	
}
