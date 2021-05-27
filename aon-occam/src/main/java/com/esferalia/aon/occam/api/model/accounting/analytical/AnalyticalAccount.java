package com.esferalia.aon.occam.api.model.accounting.analytical;

import java.io.Serializable;

public class AnalyticalAccount  implements Serializable {
	
	private static final long serialVersionUID = -2043181011592440757L;
	
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
