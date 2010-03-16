package com.esferalia.aon.payroll.core;

import java.io.Serializable;

import com.esferalia.aon.core.IRegistry;


public interface IPersona extends Serializable{

	IRegistry getRegistry();
	void setRegistry(IRegistry registry);

	String getName();
	void setName(String name);

	String getSurname();
	void setSurname(String surname);

	String getLastName();
	void setLastName(String lastName);
	
}
