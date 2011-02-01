package com.esferalia.aon.payroll.core;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.payroll.core.cotizacion.ITipoBonificacion;

public interface IBonificacion extends Serializable{

	Serializable getId();
	
	Date getFechaFin();
	void setFechaFin(Date fechaFin);
	
	Integer getHoras();
	void setHoras(Integer horas);
	
	Double getImporte();
	void setImporte(Double importe);
	
	IEmpleado getEmpleado();
	void setEmpleado(IEmpleado empleado);

	ITipoBonificacion getTipoBonificacion();
	void setTipoBonificacion(ITipoBonificacion tipoBonificacion);
	
}
