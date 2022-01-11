package com.esferalia.aon.omega;

public class Address {
	
	Integer address;			// Address Id (se rellena en el proceso)

	String streetType;			// Tipo de via ej: 'CL' -> 'Calle'
	String street;				// Direccion
	String number;				// Numero direccion
	String zip;					// Codigo Postal
	String city;				// Localidad
	String geozone;				// Codigo zona geografica ej: 01 -> ALAVA
	String municipalityCode;	// Codigo municipio ej: 01059 -> VITORIA
	
	protected Address() {
		super();
	}

	public Integer getAddress() {
		return address;
	}

	public Address setAddress(Integer address) {
		this.address = address;
		return this;
	}

	public String getStreetType() {
		return streetType;
	}

	public Address setStreetType(String streetType) {
		this.streetType = streetType;
		return this;
	}

	public String getStreet() {
		return street;
	}

	public Address setStreet(String street) {
		this.street = street;
		return this;
	}

	public String getNumber() {
		return number;
	}

	public Address setNumber(String number) {
		this.number = number;
		return this;
	}

	public String getZip() {
		return zip;
	}

	public Address setZip(String zip) {
		this.zip = zip;
		return this;
	}

	public String getCity() {
		return city;
	}

	public Address setCity(String city) {
		this.city = city;
		return this;
	}

	public String getGeozone() {
		return geozone;
	}

	public Address setGeozone(String geozone) {
		this.geozone = geozone;
		return this;
	}

	public String getMunicipalityCode() {
		return municipalityCode;
	}

	public Address setMunicipalityCode(String municipalityCode) {
		this.municipalityCode = municipalityCode;
		return this;
	}
	
}
