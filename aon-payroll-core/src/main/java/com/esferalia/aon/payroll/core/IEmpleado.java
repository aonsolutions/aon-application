package com.esferalia.aon.payroll.core;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.payroll.core.enumeration.TipoContrato;
import com.esferalia.aon.payroll.core.enumeration.CuentaCotizacion;

public interface IEmpleado extends Serializable{

	IEmpresa getEmpresa();
	void setEmpresa(IEmpresa empresa);
	
	IPersona getPersona();
	void setPersona(IPersona persona);
	
	Date getFechaInicio();
	void setFechaInicio();

	Date getFechaFin();
	void setFechaFin();
	
	CuentaCotizacion getCuentaCotizacion();
	void setCuentaCotizacion(CuentaCotizacion CuentaCotizacion);
	
	boolean isMayor65();
	void setMayor65(boolean mayor65);
	
	TipoContrato getTipoContrato();
	void setTipoContrato(TipoContrato tipoContrato);
	
}
