package com.esferalia.aon.gwt.payroll.shared;

public enum AggregatedAnnualSummaryService {
	ENTERPRISE("enterpriseId"),
	WORKPLACE("workplaceId"),
	YEAR("year");
	private String name;
	private AggregatedAnnualSummaryService (String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	
}