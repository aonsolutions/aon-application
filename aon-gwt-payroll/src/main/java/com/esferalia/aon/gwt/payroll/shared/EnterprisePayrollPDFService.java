package com.esferalia.aon.gwt.payroll.shared;

public class EnterprisePayrollPDFService {
	public static enum Params {
		ENTERPRISE("enterpriseId"),
		WORKPLACE("workplaceId"),
		MONTH("month"),
		YEAR("year"),
		SELECTED_SALARIES("selectedSalaries"),
		FILTER("filter"),
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
