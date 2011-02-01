package com.esferalia.aon.payroll.core;

import java.io.Serializable;

public interface ICliente extends Serializable{

	Integer getId();
	void setId(Integer id);

	String getInactivoBD();
	void setInactivoBD(String inactivoBD);
	
	boolean isInactivo();
	void setInactivo(boolean inactivo);
	
}
