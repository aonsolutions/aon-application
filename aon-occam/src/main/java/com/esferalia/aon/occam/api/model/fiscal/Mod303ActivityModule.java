package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class Mod303ActivityModule implements Serializable {

	private static final long serialVersionUID = 6080727858224392417L;
	
	private String description;
	private double value;
	private String unit;
	private double factor;
	private double result;
	
	private boolean salariedStaff;
	private boolean noSalariedStaff;
	
	public String getDescription() {
		return description;
	}
	public Mod303ActivityModule setDescription(String description) {
		this.description = description;
		return this;
	}

	public double getValue() {
		return value;
	}
	public Mod303ActivityModule setValue(double value) {
		this.value = value;
		return this;
	}

	public String getUnit() {
		return unit;
	}
	public Mod303ActivityModule setUnit(String unit) {
		this.unit = unit;
		return this;
	}

	public double getFactor() {
		return factor;
	}
	public Mod303ActivityModule setFactor(double factor) {
		this.factor = factor;
		return this;
	}

	public double getResult() {
		return result;
	}
	public Mod303ActivityModule setResult(double result) {
		this.result = result;
		return this;
	}

	public boolean isSalariedStaff() {
		return salariedStaff;
	}
	public Mod303ActivityModule setSalariedStaff(boolean salariedStaff) {
		this.salariedStaff = salariedStaff;
		return this;
	}

	public boolean isNoSalariedStaff() {
		return noSalariedStaff;
	}
	public Mod303ActivityModule setNoSalariedStaff(boolean noSalariedStaff) {
		this.noSalariedStaff = noSalariedStaff;
		return this;
	}
	
	public boolean isStaff() {
		return isSalariedStaff() || isNoSalariedStaff();
	}

	public static Mod303ActivityModule clone(Mod303ActivityModule toClone) {
		return new Mod303ActivityModule()
			.setDescription(toClone.getDescription())
			.setValue(toClone.getValue())
			.setUnit(toClone.getUnit()) 
			.setFactor(toClone.getFactor())
			.setResult(toClone.getResult())
			.setSalariedStaff(toClone.isSalariedStaff())
			.setNoSalariedStaff(toClone.isNoSalariedStaff())
			;
	}
	

}
