package com.esferalia.aon.payroll.core;

import java.io.Serializable;

import com.esferalia.aon.core.IRegistry;

public interface IEmpresa extends Serializable{

	Integer getId();
	void setId(Integer id);

	IRegistry getRegistry();
	void setRegistry(IRegistry registry);
	
	String getName();
	void setName(String name);
	
	ICliente getCliente();
	void setCliente(ICliente cliente);
	
	boolean isActive();
	
}
