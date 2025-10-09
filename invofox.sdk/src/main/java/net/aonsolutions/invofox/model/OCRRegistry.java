package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Optional;

public class OCRRegistry implements Serializable {
	
	private static final long serialVersionUID = 6531390994637826794L;
	
	private OCRString country;
	private OCRString name;
	private OCRString taxId;
	private OCRAddress address;
	private OCRString email;
	private OCRString phoneNumber;
	private OCRString fax;
	private OCRString website;
	
	public Optional<OCRString> getCountry() {
		return Optional.ofNullable(country);
	}
	
	public OCRRegistry setCountry(OCRString country) {
		this.country = country;
		return this;
	}
	
	public Optional<OCRString> getName() {
		return Optional.ofNullable(name);
	}
	
	public OCRRegistry setName(OCRString name) {
		this.name = name;
		return this;
	}
	
	public Optional<OCRString> getTaxId() {
		return Optional.ofNullable(taxId);
	}
	
	public OCRRegistry setTaxId(OCRString taxId) {
		this.taxId = taxId;
		return this;
	}
	
	public Optional<OCRAddress> getAddress() {
		return Optional.ofNullable(address);
	}
	
	public OCRRegistry setAddress(OCRAddress address) {
		this.address = address;
		return this;
	}
	
	public Optional<OCRString> getEmail() {
		return Optional.ofNullable(email);
	}
	
	public OCRRegistry setEmail(OCRString email) {
		this.email = email;
		return this;
	}	
	
	public Optional<OCRString> getPhoneNumber() {
		return Optional.ofNullable(phoneNumber);
	}
	
	public OCRRegistry setPhoneNumber(OCRString phoneNumber) {
		this.phoneNumber = phoneNumber;
		return this;
	}
	
	public Optional<OCRString> getFax() {
		return Optional.ofNullable(fax);
	}
	
	public OCRRegistry setFax(OCRString fax) {
		this.fax = fax;
		return this;
	}
	
	public Optional<OCRString> getWebsite() {
		return Optional.ofNullable(website);
	}
	
	public OCRRegistry setWebsite(OCRString website) {
		this.website = website;
		return this;
	}
	
}
