package com.esferalia.aon.payroll.core.empresa;

import java.util.Date;

import com.esferalia.aon.payroll.core.IEmpresa;

public interface IRemesaCertificadoEmpresa {
	
	Integer getId();
	void setId(Integer  id);
	
	IEmpresa getEmpresa();
	void setEmpresa(IEmpresa empresa);
	
	Integer getNumeroCcc();
	void setNumeroCcc(Integer numeroCcc);
	
	Date getFecha();
	void setFecha(Date fecha);
	
	Integer getEstado();
	void setEstado(Integer estado);
	
	String getHuella();
	void setHuella(String huella);
	
}
