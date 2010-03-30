package com.esferalia.aon.payroll.core;

import java.util.Date;

import com.code.aon.common.ITransferObject;
import com.esferalia.aon.payroll.core.enumeration.CuentaCotizacion;

public interface IEmpleado extends ITransferObject{

	Integer getId();
	void setId(Integer  id);

	IEmpresa getEmpresa();
	void setEmpresa(IEmpresa empresa);
	
	IActividad getActividad();
	void setActividad(IActividad actividad);

	IPersona getPersona();
	void setPersona(IPersona persona);
	
	Date getFechaInicio();
	void setFechaInicio(Date fechaInicio);

	Date getFechaFin();
	void setFechaFin(Date fechaFin);
	
	CuentaCotizacion getCuentaCotizacion();
	void setCuentaCotizacion(CuentaCotizacion CuentaCotizacion);
	
	boolean isMayor65();
	void setMayor65(boolean mayor65);
	
}
