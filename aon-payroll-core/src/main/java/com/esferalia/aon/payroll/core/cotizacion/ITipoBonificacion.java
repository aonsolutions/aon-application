package com.esferalia.aon.payroll.core.cotizacion;

import com.code.aon.common.ITransferObject;


public interface ITipoBonificacion extends ITransferObject{
	
	Integer getId();
	void setId(Integer id);
	
	String getDescripcion();
	void setDescripcion(String description);

}
