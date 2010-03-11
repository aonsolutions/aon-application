package com.esferalia.aon.payroll.core;

import java.io.Serializable;

import com.esferalia.aon.core.IDocument;
import com.esferalia.aon.core.IRegistry;

public interface IEmpresa<R extends IRegistry<IDocument>> extends Serializable{

	R getRegistry();
	void setRegistry(R registry);
	
	String getName();
	void setName(String name);
	
	boolean isActive();
	
}
