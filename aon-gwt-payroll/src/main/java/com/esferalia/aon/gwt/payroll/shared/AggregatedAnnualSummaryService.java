package com.esferalia.aon.gwt.payroll.shared;

public class AggregatedAnnualSummaryService {
	public static enum Params {
		ENTERPRISE("enterpriseId"),
		WORKPLACE("workplaceId"),
		YEAR("year"),
		COMPLETE("complete"),
		DOMAIN("domain"),
		USER("user");
		private String name;
		private Params (String name) {
			this.name = name;
		}
		public String getName() {
			return name;
		}
		
	}
}