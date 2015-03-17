package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;


public class FiscalActivityModule implements Serializable {
	
	private static final long serialVersionUID = 3925928768874434056L;
	
	private Integer id;
	private Integer fiscalActivity;
	private FiscalActivityInfoKey infoKey;
	private int line;
	private String value;
	private double factor;
	private double base;
	private String unit;
	private double minValue;
	private double maxValue;
	
	public Integer getId() {
		return id;
	}
	public FiscalActivityModule setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getFiscalActivity() {
		return fiscalActivity;
	}
	public FiscalActivityModule setFiscalActivity(Integer fiscalActivity) {
		this.fiscalActivity = fiscalActivity;
		return this;
	}
	
	public FiscalActivityInfoKey getInfoKey() {
		return infoKey;
	}
	public FiscalActivityModule setInfoKey(FiscalActivityInfoKey infoKey) {
		this.infoKey = infoKey;
		return this;
	}
	
	public int getLine() {
		return line;
	}
	public FiscalActivityModule setLine(int line) {
		this.line = line;
		return this;
	}
	
	public String getValue() {
		return value;
	}
	public FiscalActivityModule setValue(String value) {
		this.value = value;
		return this;
	}

	public double getFactor() {
		return factor;
	}
	public FiscalActivityModule setFactor(double factor) {
		this.factor = factor;
		return this;
	}

	public double getBase() {
		return base;
	}
	public FiscalActivityModule setBase(double base) {
		this.base = base;
		return this;
	}
	
	public String getUnit() {
		return unit;
	}
	public FiscalActivityModule setUnit(String unit) {
		this.unit = unit;
		return this;
	}
	
	public double getMinValue() {
		return minValue;
	}
	public FiscalActivityModule setMinValue(double minValue) {
		this.minValue = minValue;
		return this;
	}
	
	public double getMaxValue() {
		return maxValue;
	}
	public FiscalActivityModule setMaxValue(double maxValue) {
		this.maxValue = maxValue;
		return this;
	}
}

