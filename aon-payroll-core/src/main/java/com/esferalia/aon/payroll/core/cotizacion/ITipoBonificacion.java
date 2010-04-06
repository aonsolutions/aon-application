package com.esferalia.aon.payroll.core.cotizacion;

import com.code.aon.common.ITransferObject;


public interface ITipoBonificacion extends ITransferObject{
	
	Integer getId();
	void setId(Integer id);
	
	String getDescription();
	void setDescription(String description);

}
