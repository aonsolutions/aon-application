package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public class UteForeign implements Serializable {

	private static final long serialVersionUID = 3400183583793598978L;
	
	private String identification;
	private String country;
	private double volume;
	private double pyg;
	private double adjust;
	private double deduction;
	

	public String getIdentification() {
		return identification;
	}

	public UteForeign setIdentification(String identification) {
		this.identification = identification;
		return this;
	}

	public String getCountry() {
		return country;
	}

	public UteForeign setCountry(String country) {
		this.country = country;
		return this;
	}

	public double getVolume() {
		return volume;
	}

	public UteForeign setVolume(double volume) {
		this.volume = volume;
		return this;
	}

	public double getPyg() {
		return pyg;
	}

	public UteForeign setPyg(double pyg) {
		this.pyg = pyg;
		return this;
	}

	public double getAdjust() {
		return adjust;
	}

	public UteForeign setAdjust(double adjust) {
		this.adjust = adjust;
		return this;
	}

	public double getDeduction() {
		return deduction;
	}

	public UteForeign setDeduction(double deduction) {
		this.deduction = deduction;
		return this;
	}
	
}
