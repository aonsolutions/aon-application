package com.esferalia.aon.payroll.core;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.payroll.core.cotizacion.IBaseCotizacion;

public interface ITrabajo extends Serializable{

	Serializable getId();

	IBaseCotizacion getBaseCotizacion();
	void setBaseCotizacion(IBaseCotizacion baseCotizacion);

	IContratosTc2 getContratoTc2();
	void setContratoTc2(IContratosTc2 contratoTc2);

	Date getFechaInicioCont();
	void setFechaInicioCont(Date fechaInicioCont);

	Date getFechaFinCont();
	void setFechaFinCont(Date fechaFinCont);

	String getCno();
	void setCno(String cno);

	

	
}
