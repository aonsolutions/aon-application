package com.esferalia.aon.gwt.payroll.shared;

public interface PayrollPrintService {
	
	public static enum PayrollType {
		AON ("aon"),
		CLASSIC ("classic");
		
		private String name;
		
		private PayrollType (String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
		
	}
	public static enum Parameter {
		TYPE ("type"),
		ENTERPRISE ("enterprise"),
		ID ("id"),
		NAME ("name"),
		DOMAIN ("domain"),
		USER ("user"),
		COMPLEMENTARY_LIMIT("complementary_limit"),
		PAYROLL_TYPE("payroll_type");
		
		private String name;
		
		private Parameter (String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
		
	}
}
