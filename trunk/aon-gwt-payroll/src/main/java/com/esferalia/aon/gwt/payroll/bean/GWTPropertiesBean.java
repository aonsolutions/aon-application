package com.esferalia.aon.gwt.payroll.bean;

import com.code.aon.master.Up2DateDB;

public class GWTPropertiesBean {
	
	public boolean isEnabled() {
		String enabled =  System.getProperty("gwt.enabled", "false");
		return enabled.equalsIgnoreCase("true");
	}
	
	public String getMenuIntegral() {
		return System.getProperty("gwt.menu.integral", "Integral (default)");
	}

}
