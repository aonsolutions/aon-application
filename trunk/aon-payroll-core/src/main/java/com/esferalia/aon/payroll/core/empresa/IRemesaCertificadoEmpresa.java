package com.esferalia.aon.payroll.core.empresa;

import java.util.Date;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.IEmpresa;

public interface IRemesaCertificadoEmpresa extends ITransferObject {
	
	Integer getId();
	void setId(Integer  id);
	
	IEmpresa getEmpresa();
	void setEmpresa(IEmpresa empresa);
	
	String getCodigoCcc();
	void setCodigoCcc(String codigoCcc);
	
	Date getFecha();
	void setFecha(Date fecha);
	
	Integer getEstado();
	void setEstado(Integer estado);
	
	String getHuella();
	void setHuella(String huella);
	
}
