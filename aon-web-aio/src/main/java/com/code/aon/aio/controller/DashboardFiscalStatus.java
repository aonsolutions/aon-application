package com.code.aon.aio.controller;

import java.util.Map;

import com.code.aon.fiscal.enumeration.Period;

public class DashboardFiscalStatus {
	
	private Period period;
	private Map<String,Integer> models;

	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
		this.period = period;
	}
	public Map<String, Integer> getModels() {
		return models;
	}
	public void setModels(Map<String, Integer> models) {
		this.models = models;
	}
	
}
