package com.esferalia.aon.gwt.payroll.bean;

public class GWTPropertiesBean {
	
	public boolean isEnabled() {
		String enabled =  System.getProperty("gwt.enabled", "false");
		return enabled.equalsIgnoreCase("true");
	}

}
