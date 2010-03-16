package com.esferalia.aon.payroll.core;

import com.esferalia.aon.payroll.core.enumeration.TipoContrato;

public interface ITrabajo {
	IEmpleado getEmpleado();
	void setEmpleado(IEmpleado empleado);
	
	TipoContrato getTipoContrato();
	void setTipoContrato(TipoContrato tipoContrato);
	
}
