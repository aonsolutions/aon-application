package com.esferalia.aon.gwt.payroll.shared;

public class RemunerationRecordService {
	public static enum Params {
		ENTERPRISE("enterpriseId"),
		YEAR("year"),
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
