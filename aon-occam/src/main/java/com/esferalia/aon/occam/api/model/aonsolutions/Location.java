package com.esferalia.aon.occam.api.model.aonsolutions;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Domain;

public class Location {

	private Integer id;
	private Domain domain;
	private String description;
	private Coordinates coordinates;
	private Integer radio;
	
	public Location() {
	
	}

	public Integer getId() {
		return id;
	}

	public Location setId(Integer id) {
		this.id = id;
		return this;
	}

	public Domain getDomain() {
		return domain;
	}

	public Location setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Location setDescription(String description) {
		this.description = description;
		return this;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}

	public Location setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
		return this;
	}
	
	public Integer getRadio() {
		return radio;
	}

	public Location setRadio(Integer radio) {
		this.radio = radio;
		return this;
	}	
	
	public JSONObject toJSON() {
		JSONObject json = new JSONObject();
		json.put("id", getId());
		json.put("radio", getRadio());
		json.put("description", getDescription());

		if(getCoordinates() != null) {
			JSONObject coordinates = getCoordinates().toJSON();
			coordinates.put("latitude", getCoordinates().getLatitude());
			coordinates.put("longitude", getCoordinates().getLongitude());
			json.put("coordinates", coordinates);
		}
		return json;
	}
}
