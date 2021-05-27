package com.esferalia.aon.gwt.payroll.shared;

public class CostExcelService {
	public static enum Params {
		ENTERPRISE("enterpriseId"),
		WORKPLACE("workplaceId"),
		MONTH("month"),
		YEAR("year"),
		EXCEL_TYPE("excelType"),
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
