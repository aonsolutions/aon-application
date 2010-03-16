package com.esferalia.aon.payroll.core.it;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.enumeration.Periodicidad;
import com.esferalia.aon.payroll.core.enumeration.TipoContingencia;

public interface IParteIT extends Serializable{

	IEmpleado getEmpleado();
	void setEmpleado(IEmpleado empleado);
	
	Date getFechaBaja();
	void setFechaBaja(Date fechaBaja);
	
	String getNumeroColegiadoBaja();
	void setNumeroColegiadoBaja(String numeroColegiadoAlta);
	
	String getCiasBaja();
	void setCiasBaja(String startMedicalAreaId);
	
	boolean isBajaProcesada();
	void setBajaProcesada(boolean bajaProcesada);

	Date getFechaAlta();
	void setFechaAlta(Date fechaAlta);

	String getNumeroColegiadoAlta();
	void setNumeroColegiadoAlta(String numeroColegiadoAlta);
	
	String getCiasAlta();
	void setCiasAlta(String ciasAlta);

	boolean isAltaProcesada();
	void setAltaProcesada(boolean altaProcesada);

	boolean isProcesada();
	void setProcesada(boolean procesada);
	
	TipoContingencia getTipoContingencia();
	void setTipoContingencia(TipoContingencia tipoContingencia);
	
	boolean isRecaida();
	void setRecaida(boolean recaida);
	
	IParteIT getParteITRecaida();
	void setParteITRecaida(IParteIT parteITRecaida);

	Periodicidad getProrrateoCotizacion();
	void setProrrateoCotizacion(Periodicidad period);
	
	Double getBaseRetribucionPeriodoAnterior();
	void setBaseRetribucionPeriodoAnterior(Double baseRetribucionPeriodoAnterior);
	
	Integer getDiasPeriodoAnterior();
	void setDiasPeriodoAnterior(Integer diasPeriodoAnterior);
	
	Double getBaseReguladoraDiaria();
	void setBaseReguladoraDiaria(Double dailyRegulatoryBase);
	
	Double getBaseDiariaContingenciasComunes();
	void setBaseDiariaContingenciasComunes(Double baseDiariaContingenciasComunes);
	
	Double getBaseDiariaAccidentesTrabajo();
	void setBaseDiariaAccidentesTrabajo(Double baseDiariaAccidentesTrabajo);
	
	Double getPrestacionDiaria60();
	void setPrestacionDiaria60(Double prestacionDiaria60);
	
	Double getPrestacionDiaria75();
	void setPrestacionDiaria75(Double prestacionDiaria75);
	
}
