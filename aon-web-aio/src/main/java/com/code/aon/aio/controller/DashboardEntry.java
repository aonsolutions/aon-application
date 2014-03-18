package com.code.aon.aio.controller;

import java.io.Serializable;
import java.util.List;

import com.code.aon.AonVersion;

public class DashboardEntry implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private String name;
	private double value;
	private String[] seriesKeys;
	private double[] seriesValues;
	private List<Integer> types ;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public String[] getSeriesKeys() {
		return seriesKeys;
	}
	public void setSeriesKeys(String[] seriesKeys) {
		this.seriesKeys = seriesKeys;
	}
	public double[] getSeriesValues() {
		return seriesValues;
	}
	public void setSeriesValues(double[] seriesValues) {
		this.seriesValues = seriesValues;
	}
	
	public List<Integer> getTypes() {
		return types;
	}
	
	public void setTypes(List<Integer> types) {
		this.types = types;
	}
	
}
