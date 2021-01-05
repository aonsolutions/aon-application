package com.esferalia.aon.occam.api.model.aonsolutions;

public class Coordinates {

	private Double latitude;
	private Double longitude;
	
	
	public Coordinates() {
	
	}
	
	public Coordinates(String coordinates) {
		String[] c = coordinates.split(",");
		this.latitude = Double.parseDouble(c[0]);
		this.longitude = Double.parseDouble(c[1]);
	}
	
	public Coordinates(Double latitude, Double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public String getCoordinates() {
		return getLatitude() + "," + getLongitude() ;
	}

	public Coordinates setCoordinates(String coordinates) {
		String[] c = coordinates.split(",");
		this.latitude = Double.parseDouble(c[0]);
		this.longitude = Double.parseDouble(c[1]);
		return this;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	
	public Coordinates setLatitude(Double latitude) {
		this.latitude = latitude;
		return this;
	}
	
	public Double getLongitude() {
		return longitude;
	}
	
	public Coordinates setLongitude(Double longitude) {
		this.longitude = longitude;
		return this;
	}
	
}
