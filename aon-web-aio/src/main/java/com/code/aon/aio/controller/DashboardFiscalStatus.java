package com.code.aon.aio.controller;

import java.io.Serializable;
import java.util.Map;

import com.code.aon.common.AonVersion;
import com.code.aon.fiscal.enumeration.Period;

public class DashboardFiscalStatus implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
