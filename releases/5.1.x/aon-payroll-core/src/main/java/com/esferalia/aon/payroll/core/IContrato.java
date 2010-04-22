package com.esferalia.aon.payroll.core;

import com.esferalia.aon.payroll.core.enumeration.Periodicidad;
import com.esferalia.aon.payroll.core.enumeration.TipoContrato;

public interface IContrato {
	IEmpleado getEmpleado();
	void setEmpleado(IEmpleado empleado);
	
	TipoContrato getTipoContrato();
	void setTipoContrato(TipoContrato tipoContrato);
	
	
	Periodicidad getProrrateoCotizacion();
	void setProrrateoCotizacion(Periodicidad period);
	
}
