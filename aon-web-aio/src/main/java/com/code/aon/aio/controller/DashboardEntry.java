package com.code.aon.aio.controller;

public class DashboardEntry {

	private String name;
	private double value;
	private String[] seriesKeys;
	private double[] seriesValues;

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
}
