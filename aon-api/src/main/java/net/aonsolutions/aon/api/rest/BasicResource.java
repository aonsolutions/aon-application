package net.aonsolutions.aon.api.rest;

import org.json.JSONArray;
import org.json.JSONObject;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class BasicResource {

	protected Response response(JSONObject json) {
		return Response.ok(json.toString())
		.type(MediaType.APPLICATION_JSON)
		.build();
	}
	
	protected Response response(JSONArray json) {
		return Response.ok(json.toString())
		.type(MediaType.APPLICATION_JSON)
		.build();
	}
}
