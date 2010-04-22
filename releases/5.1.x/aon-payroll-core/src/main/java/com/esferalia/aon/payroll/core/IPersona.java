package com.esferalia.aon.payroll.core;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.core.IRegistry;


public interface IPersona extends ITransferObject{

	Integer getId();
	void setId(Integer id);
	
	IRegistry getRegistry();
	void setRegistry(IRegistry registry);

	String getName();
	void setName(String name);

	String getSurname();
	void setSurname(String surname);

	String getLastName();
	void setLastName(String lastName);
	
	String getNumSS();
	void setNumSS(String numSS);
	
	String getFullName();
}