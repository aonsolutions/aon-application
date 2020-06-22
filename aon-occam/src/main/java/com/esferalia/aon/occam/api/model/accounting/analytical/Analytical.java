package com.esferalia.aon.occam.api.model.accounting.analytical;

import java.util.TreeMap;

public class Analytical {

	private String name;
	private String defaultCostCenter;
	private TreeMap<String, AnalyticalCostCenter> costCenters;
	
	public String getName() {
		return name;
	}
	public Analytical setName(String name) {
		this.name = name;
		return this;
	}
	
	public TreeMap<String, AnalyticalCostCenter> getCostCenters() {
		if (costCenters == null) {
			setCostCenters(new TreeMap<String, AnalyticalCostCenter>());
		}
		return costCenters;
	}

	public Analytical setCostCenters(TreeMap<String, AnalyticalCostCenter> costCenters) {
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

}
