package com.esferalia.aon.occam.api.model.accounting.analytical;

public class AnalyticalAccount {
	private String code;
	private double percent;

	public String getCode() {
		return code;
	}

	public AnalyticalAccount setCode(String code) {
		this.code = code;
		return this;
	}

	public double getPercent() {
		return percent;
	}

	public AnalyticalAccount setPercent(double percent) {
		this.percent = percent;
		return this;
	}

}
