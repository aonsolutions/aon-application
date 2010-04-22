package com.esferalia.aon.payroll.core;

import java.io.Serializable;

import com.esferalia.aon.payroll.core.enumeration.CuentaCotizacion;


/**
 * Códigos de Cuentas de cotizacion por actividades.
 *
 */
public interface IActividadCCC extends Serializable{

	IActividad getActividad();
	void setActividad(IActividad actividad);
	
	CuentaCotizacion getCuentaCotizacion();
	void setCuentaCotizacion(CuentaCotizacion cuentaCotizacion);
	
	String getDescripcion();
	void setDescripcion(String descripcion);
	
}
