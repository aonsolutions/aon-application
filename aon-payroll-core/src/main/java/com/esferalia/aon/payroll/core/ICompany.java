package com.esferalia.aon.payroll.core;

public interface ICompany {

	IRegistry getRegistry();
	void setRegistry(IRegistry registry);
	
	String getName();
	void setName(String name);
	
}
