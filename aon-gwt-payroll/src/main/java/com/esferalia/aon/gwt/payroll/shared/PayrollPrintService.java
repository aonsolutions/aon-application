package com.esferalia.aon.gwt.payroll.shared;

public interface PayrollPrintService {
	public static enum Parameter {
		TYPE ("type"),
		ENTERPRISE ("enterprise"),
		ID ("id"),
		NAME ("name"),
		DOMAIN ("domain"),
		USER ("user");
		
		private String name;
		
		private Parameter (String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
	}
}
