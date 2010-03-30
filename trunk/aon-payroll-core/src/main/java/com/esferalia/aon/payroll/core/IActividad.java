package com.esferalia.aon.payroll.core;

import java.io.Serializable;

import com.esferalia.aon.payroll.core.enumeration.Regimen;

public interface IActividad extends Serializable{

	IEmpresa getEmpresa();
	void setEmpresa(IEmpresa empresa);
	
	String getName();
	void setName(String name);
	
	Regimen getRegimen();
	void setRegimen(Regimen regimen);
	
}
