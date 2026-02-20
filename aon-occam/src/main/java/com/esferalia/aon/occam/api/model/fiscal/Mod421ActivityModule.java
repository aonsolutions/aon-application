package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class Mod421ActivityModule implements Serializable {

	private static final long serialVersionUID = -1411548474727838828L;
	
	private String description;
	private double value;
	private String unit;
	private double factor;
	private double result;
//	private boolean salariedStaff;
//	private boolean noSalariedStaff;
	
	public String getDescription() {
		return description;
	}
	public Mod421ActivityModule setDescription(String description) {
		this.description = description;
		return this;
	}

	public double getValue() {
		return value;
	}
	public Mod421ActivityModule setValue(double value) {
		this.value = value;
		return this;
	}

	public String getUnit() {
		return unit;
	}
	public Mod421ActivityModule setUnit(String unit) {
		this.unit = unit;
		return this;
	}

	public double getFactor() {
		return factor;
	}
	public Mod421ActivityModule setFactor(double factor) {
		this.factor = factor;
		return this;
	}

	public double getResult() {
		return result;
	}
	public Mod421ActivityModule setResult(double result) {
		this.result = result;
		return this;
	}

//	public boolean isSalariedStaff() {
//		return salariedStaff;
//	}
//	public Mod421ActivityModule setSalariedStaff(boolean salariedStaff) {
//		this.salariedStaff = salariedStaff;
//		return this;
//	}
//
//	public boolean isNoSalariedStaff() {
//		return noSalariedStaff;
//	}
//	public Mod421ActivityModule setNoSalariedStaff(boolean noSalariedStaff) {
//		this.noSalariedStaff = noSalariedStaff;
//		return this;
//	}
	
//	public boolean isStaff() {
//		return isSalariedStaff() || isNoSalariedStaff();
//	}

	public static Mod421ActivityModule clone(Mod421ActivityModule toClone) {
		return new Mod421ActivityModule()
			.setDescription(toClone.getDescription())
			.setValue(toClone.getValue())
			.setUnit(toClone.getUnit()) 
			.setFactor(toClone.getFactor())
			.setResult(toClone.getResult())
//			.setSalariedStaff(toClone.isSalariedStaff())
//			.setNoSalariedStaff(toClone.isNoSalariedStaff())
			;
	}
	

}
