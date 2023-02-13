package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.List;

public class Country implements Serializable {

	private static final long serialVersionUID = -217013833703035323L;
	
	private Geozone country;
	private List<Geozone> provinces;
	
	public Country() {
		super();
	}
	
	public Geozone getCountry() {
		return country;
	}
	public Country setCountry(Geozone country) {
		this.country = country;
		return this;
	}
	public List<Geozone> getProvinces() {
		return provinces;
	}
	public Country setProvinces(List<Geozone> provinces) {
		this.provinces = provinces;
		return this;
	}
	
}
