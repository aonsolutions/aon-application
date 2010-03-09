package com.esferalia.aon.payroll.core;

import java.io.Serializable;

import com.esferalia.aon.core.IRegistry;

public interface IEmpresa extends Serializable{

	IRegistry getRegistry();
	void setRegistry(IRegistry registry);
	
	String getName();
	void setName(String name);
	
	boolean isActive();
	
}
