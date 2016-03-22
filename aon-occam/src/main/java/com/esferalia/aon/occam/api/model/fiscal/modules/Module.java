package com.esferalia.aon.occam.api.model.fiscal.modules;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.fiscal.FiscalActivityInfoKey;

public class Module implements Serializable {

	private static final long serialVersionUID = 2899951335919767090L;
	
	private int line;
	private FiscalActivityInfoKey key;
	private String unit;
	private double amount;

	public Module(int line, FiscalActivityInfoKey key, String unit, double amount) {
		this.line = line;
		this.key = key;
		this.unit = unit;
		this.amount = amount;
	}

	public int getLine() {
		return line;
	}

	public FiscalActivityInfoKey getKey() {
		return key;
	}

	public String getUnit() {
		return unit;
	}

	public double getAmount() {
		return amount;
	}
	
	public boolean isSalariedStaff() {
		return getKey() == FiscalActivityInfoKey.M01
			|| getKey() == FiscalActivityInfoKey.M15
			|| getKey() == FiscalActivityInfoKey.M16;
	}
	public boolean isNoSalariedStaff() {
		return getKey() == FiscalActivityInfoKey.M02;
	}
}