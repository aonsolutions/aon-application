package com.esferalia.aon.payroll.core;


public interface IPerson {

	IRegistry getRegistry();
	void setRegistry(IRegistry registry);

	String getName();
	void setName(String name);

	String getSurname();
	void setSurname(String surname);

	String getLastName();
	void setLastName(String lastName);
	
}
