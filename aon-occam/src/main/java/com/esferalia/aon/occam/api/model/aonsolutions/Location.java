package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.Objects;

import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.Domain;

public class Location implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Domain domain;
	private String description;
	private Coordinates coordinates;
	private Integer radio;
	
	private TimeControlReason type;
	private Integer registry;
	
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
	
	public TimeControlReason getType() {
		return type;
	}

	public Location setType(TimeControlReason type) {
		this.type = type;
		return this;
	}

	public Integer getRegistry() {
		return registry;
	}

	public Location setRegistry(Integer registry) {
		this.registry = registry;
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
		
		if(null != getType())
			json.put("type", getType().value());
		
		if(null != getRegistry())
			json.put("registry", getRegistry());
		
		return json;
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Location ) )
			return false;
		
		Location loc = (Location) obj;
		
		return Objects.equals(id, loc.id);
	}
}
