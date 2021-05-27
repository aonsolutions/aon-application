package com.esferalia.aon.occam.api.model.accounting.analytical;

import java.io.Serializable;
import java.util.LinkedHashMap;

public class Analytical implements Serializable {

	private static final long serialVersionUID = 3481819751297188336L;
	
	private String name;
	private String defaultCostCenter;
	private LinkedHashMap<String, AnalyticalCostCenter> costCenters;
	
	public String getName() {
		return name;
	}
	public Analytical setName(String name) {
		this.name = name;
		return this;
	}
	
	public LinkedHashMap<String, AnalyticalCostCenter> getCostCenters() {
		if (costCenters == null) {
			setCostCenters(new LinkedHashMap<String, AnalyticalCostCenter>());
		}
		return costCenters;
	}

	public Analytical setCostCenters(LinkedHashMap<String, AnalyticalCostCenter> costCenters) {
		this.costCenters = costCenters;
		return this;
	}

	public String getDefaultCostCenter() {
		return defaultCostCenter;
	}

	public Analytical setDefaultCostCenter(String defaultCostCenter) {
		this.defaultCostCenter = defaultCostCenter;
		return this;
	}
	public void add(AnalyticalCostCenter costCenter) {
		getCostCenters().put(costCenter.getName(), costCenter);
	}
	public void remove(String costCenterName) {
		getCostCenters().remove(costCenterName);
	}

}
