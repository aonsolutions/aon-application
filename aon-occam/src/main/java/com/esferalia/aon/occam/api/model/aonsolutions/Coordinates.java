package com.esferalia.aon.occam.api.model.aonsolutions;

public class Coordinates {

	private String coordinates;
	
	
	public Coordinates() {
	
	}
	
	public Coordinates(String coordinates) {
		this.coordinates = coordinates;
	}
	
	public Coordinates(Double latitude, Double longitude) {
		this.coordinates = latitude + "," + longitude;
	}

	public String getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}
	
	public Double getLatitude() {
		String[] coordinates = getCoordinates().split(",");
		return Double.parseDouble(coordinates[0]);
	}
	
	public void setLatitude(Double latitude) {
		this.coordinates = latitude + "," + getLongitude();
	}
	
	public Double getLongitude() {
		String[] coordinates = getCoordinates().split(",");
		return Double.parseDouble(coordinates[1]);
	}
	
	public void setLongitude(Double longitude) {
		this.coordinates = getLatitude() + "," + longitude;
	}
	
}
