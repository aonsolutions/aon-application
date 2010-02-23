package com.code.aon.finance.model347;

import com.code.aon.finance.enumeration.Model347Type;
import com.code.aon.registry.RegistryDocument;

public class Model347 {

	boolean disabled;
	
	Model347Type type;
	int id;
	String name;
	RegistryDocument document;
	Integer geozone;
	String geozoneName;
	Integer country;
	String countryName;
	double total;
	public boolean isDisabled() {
		return disabled;
	}
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	public Model347Type getType() {
		return type;
	}
	public void setType(Model347Type type) {
		this.type = type;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public RegistryDocument getDocument() {
		return document;
	}
	public void setDocument(RegistryDocument document) {
		this.document = document;
	}
	public Integer getGeozone() {
		return geozone;
	}
	public void setGeozone(Integer geozone) {
		this.geozone = geozone;
	}
	public String getGeozoneName() {
		return geozoneName;
	}
	public void setGeozoneName(String geozoneName) {
		this.geozoneName = geozoneName;
	}
	public Integer getCountry() {
		return country;
	}
	public void setCountry(Integer country) {
		this.country = country;
	}
	public String getCountryName() {
		return countryName;
	}
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	

	
}
