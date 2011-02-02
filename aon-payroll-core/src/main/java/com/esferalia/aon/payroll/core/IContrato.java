package com.esferalia.aon.payroll.core;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.payroll.core.enumeration.Periodicidad;
import com.esferalia.aon.payroll.core.enumeration.TipoContrato;

public interface IContrato {
	
	Serializable getId();

	IEmpleado getEmpleado();
	void setEmpleado(IEmpleado empleado);
	
	TipoContrato getTipoContrato();
	void setTipoContrato(TipoContrato tipoContrato);
	
	Periodicidad getProrrateoCotizacion();
	void setProrrateoCotizacion(Periodicidad period);

	Date getFechaFin();
	void setFechaFin(Date fechaFin);

	String getIndtp();
	void setIndtp(String indtp);

	Double getIrpf();
	void setIrpf(Double irpf);
	
}
