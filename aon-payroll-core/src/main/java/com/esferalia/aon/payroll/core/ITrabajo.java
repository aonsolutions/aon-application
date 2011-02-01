package com.esferalia.aon.payroll.core;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.payroll.core.cotizacion.IBaseCotizacion;
import com.esferalia.aon.payroll.core.enumeration.TiempoContrato;
import com.esferalia.aon.payroll.core.enumeration.TipoTiempoParcial;

public interface ITrabajo extends Serializable{

	Serializable getId();

	Date getFecini();

	Date getFecfin();
	void setFecfin(Date fecfin);

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

	IEmpleado getEmpleado();
	void setEmpleado(IEmpleado empleado);

	TiempoContrato getTiempoContrato();
	void setTiempoContrato(TiempoContrato tiempoContrato);

	TipoTiempoParcial getTipoTP();
	void setTipoTP(TipoTiempoParcial tipoTP);

	Integer getDiasTP();
	void setDiasTP(Integer diasTP);


}
