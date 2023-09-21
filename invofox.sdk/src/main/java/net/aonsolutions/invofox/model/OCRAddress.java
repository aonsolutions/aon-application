package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Optional;

public class OCRAddress implements Serializable {
	
	private static final long serialVersionUID = 9148275724492999748L;
	
	private String text;
	private String addressNumber;
	private String country;
	private String municipality;
	private String neighborhood;
	private String postalCode;
	private String region;
	private String street;
	private String subRegion;
	
	public Optional<String> getText() {
		return Optional.ofNullable(text);
	}
	public OCRAddress setText(String text) {
		this.text = text;
		return this;
	}
	
	public Optional<String> getAddressNumber() {
		return Optional.ofNullable(addressNumber);
	}
	public OCRAddress setAddressNumber(String addressNumber) {
		this.addressNumber = addressNumber;
		return this;
	}
	
	public Optional<String> getCountry() {
		return Optional.ofNullable(country);
	}
	public OCRAddress setCountry(String country) {
		this.country = country;
		return this;
	}
	
	public Optional<String> getMunicipality() {
		return Optional.ofNullable(municipality);
	}
	public OCRAddress setMunicipality(String municipality) {
		this.municipality = municipality;
		return this;
	}
	
	public Optional<String> getNeighborhood() {
		return Optional.ofNullable(neighborhood);
	}
	public OCRAddress setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
		return this;
	}
	
	public Optional<String> getPostalCode() {
		return Optional.ofNullable(postalCode);
	}
	public OCRAddress setPostalCode(String postalCode) {
		this.postalCode = postalCode;
		return this;
	}
	
	public Optional<String> getRegion() {
		return Optional.ofNullable(region);
	}
	public OCRAddress setRegion(String region) {
		this.region = region;
		return this;
	}
	
	public Optional<String> getStreet() {
		return Optional.ofNullable(street);
	}
	public OCRAddress setStreet(String street) {
		this.street = street;
		return this;
	}
	
	public Optional<String> getSubRegion() {
		return Optional.ofNullable(subRegion);
	}
	public OCRAddress setSubRegion(String subRegion) {
		this.subRegion = subRegion;
		return this;
	}
	
	
}
