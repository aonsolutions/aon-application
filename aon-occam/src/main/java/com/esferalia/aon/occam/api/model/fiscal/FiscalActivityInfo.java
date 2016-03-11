package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonNumberUtils;

public class FiscalActivityInfo implements Serializable {
	
	private static final long serialVersionUID = -7095500555384381798L;
	
	private Integer id;
	private Integer fiscalActivity;
	private FiscalActivityInfoKey infoKey;
	private FiscalActivityInfoKeyType infoType;
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
	public FiscalActivityInfo setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public Integer getFiscalActivity() {
		return fiscalActivity;
	}
	public FiscalActivityInfo setFiscalActivity(Integer fiscalActivity) {
		this.fiscalActivity = fiscalActivity;
		return this;
	}
	
	public FiscalActivityInfoKey getInfoKey() {
		return infoKey;
	}
	public FiscalActivityInfo setInfoKey(FiscalActivityInfoKey infoKey) {
		this.infoKey = infoKey;
		return this;
	}
	
	public FiscalActivityInfoKeyType getInfoType() {
		return infoType;
	}
	public FiscalActivityInfo setInfoType(FiscalActivityInfoKeyType infoType) {
		this.infoType = infoType;
		return this;
	}
	
	public int getLine() {
		return line;
	}
	public FiscalActivityInfo setLine(int line) {
		this.line = line;
		return this;
	}
	
	public String getValue() {
		return value;
	}
	public FiscalActivityInfo setValue(String value) {
		this.value = value;
		return this;
	}
	public double getDoubleValue() {
		return AonNumberUtils.todouble(getValue());
	}
	
	public double getFactor() {
		return factor;
	}
	public FiscalActivityInfo setFactor(double factor) {
		this.factor = factor;
		return this;
	}

	public double getBase() {
		return base;
	}
	public FiscalActivityInfo setBase(double base) {
		this.base = base;
		return this;
	}

	public String getUnit() {
		return unit;
	}
	public FiscalActivityInfo setUnit(String unit) {
		this.unit = unit;
		return this;
	}

	public double getMinValue() {
		return minValue;
	}
	public FiscalActivityInfo setMinValue(double minValue) {
		this.minValue = minValue;
		return this;
	}

	public double getMaxValue() {
		return maxValue;
	}
	public FiscalActivityInfo setMaxValue(double maxValue) {
		this.maxValue = maxValue;
		return this;
	}
}

