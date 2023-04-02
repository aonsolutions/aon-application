package com.esferalia.aon.occam.api.model.fiscal.modules;

import java.io.Serializable;

public class Module implements Serializable {

	private static final long serialVersionUID = 2899951335919767090L;
	
	private int line;
	private ModuleInfo key;
	private String unit;
	private double amount;

	public Module(int line, ModuleInfo key, String unit, double amount) {
		this.line = line;
		this.key = key;
		this.unit = unit;
		this.amount = amount;
	}

	public int getLine() {
		return line;
	}

	public ModuleInfo getKey() {
		return key;
	}

	public String getUnit() {
		return unit;
	}

	public double getAmount() {
		return amount;
	}
	
	public boolean isSalariedStaff() {
		return getKey() == ModuleInfo.M01
			|| getKey() == ModuleInfo.M15
			|| getKey() == ModuleInfo.M16
			|| getKey() == ModuleInfo.M26 
			|| getKey() == ModuleInfo.M27 
			|| getKey() == ModuleInfo.M56 
			|| getKey() == ModuleInfo.M59 
			|| getKey() == ModuleInfo.M62;
	}
	public boolean isNoSalariedStaff() {
		return getKey() == ModuleInfo.M02;
	}
}