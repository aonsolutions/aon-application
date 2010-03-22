package com.esferalia.aon.payroll.core;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.core.IDocument;
import com.esferalia.aon.core.IRegistry;
import com.esferalia.aon.payroll.core.enumeration.TipoContrato;
import com.esferalia.aon.payroll.core.enumeration.CuentaCotizacion;

public interface IEmpleado
		<E extends IEmpresa<IRegistry<IDocument>>,
		 P extends IPersona<IRegistry<IDocument>>> extends Serializable{

	E getEmpresa();
	void setEmpresa(E empresa);
	
	P getPersona();
	void setPersona(P persona);
	
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
