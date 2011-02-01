package com.code.aon.file.tax.model.MOD340.data;


public class IntracommunitaryInvoice extends Invoice {

	private String intracommunitaryType;
	private String declaredKey;
	private String countryKey;
	private int operationPeriod;
	private String description;
	private String address;
	private String city;
	private String zip;
	private String other;

	public String getIntracommunitaryType() {
		return intracommunitaryType;
	}
	public void setIntracommunitaryType(String intracommunitaryType) {
		this.intracommunitaryType = intracommunitaryType;
	}
	public String getDeclaredKey() {
		return declaredKey;
	}
	public void setDeclaredKey(String declaredKey) {
		this.declaredKey = declaredKey;
	}
	public String getCountryKey() {
		return countryKey;
	}
	public void setCountryKey(String countryKey) {
		this.countryKey = countryKey;
	}
	public int getOperationPeriod() {
		return operationPeriod;
	}
	public void setOperationPeriod(int operationPeriod) {
		this.operationPeriod = operationPeriod;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	
}
