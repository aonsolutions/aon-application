package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Optional;

public class OCRAddress implements Serializable {
	
	private static final long serialVersionUID = 9148275724492999748L;
	
	private OCRString raw;
	private OCRString postalCode;
	private OCRString municipality;
	private OCRString region;
	private OCRString street;

	public Optional<OCRString> getRaw() {
		return Optional.ofNullable(raw);
	}
	
	public OCRAddress setRaw(OCRString raw) {
		this.raw = raw;
		return this;
	}
	
	public Optional<OCRString> getPostalCode() {
		return Optional.ofNullable(postalCode);
	}
	
	public OCRAddress setPostalCode(OCRString postalCode) {
		this.postalCode = postalCode;
		return this;
	}

	public Optional<OCRString> getMunicipality() {
		return Optional.ofNullable(municipality);
	}
	
	public OCRAddress setMunicipality(OCRString municipality) {
		this.municipality = municipality;
		return this;
	}
	
	public Optional<OCRString> getRegion() {
		return Optional.ofNullable(region);
	}
	
	public OCRAddress setRegion(OCRString region) {
		this.region = region;
		return this;
	}
	
	public Optional<OCRString> getStreet() {
		return Optional.ofNullable(street);
	}
	
	public OCRAddress setStreet(OCRString street) {
		this.street = street;
		return this;
	}
	
}
