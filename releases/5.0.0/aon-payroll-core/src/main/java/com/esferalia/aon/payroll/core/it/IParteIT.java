package com.esferalia.aon.payroll.core.it;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.core.IDocument;
import com.esferalia.aon.core.IRegistry;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IEmpresa;
import com.esferalia.aon.payroll.core.IPersona;
import com.esferalia.aon.payroll.core.enumeration.TipoContingencia;
import com.esferalia.aon.payroll.core.enumeration.Periodicidad;

public interface IParteIT
	<E extends IEmpleado<IEmpresa<IRegistry<IDocument>>,IPersona<IRegistry<IDocument>>>,
	P extends IParteIT<E,P>> extends Serializable{

	E getEmpleado();
	void setEmpleado(E empleado);
	
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
	
	P getParteITRecaida();
	void setParteITRecaida(P ParteITRecaida);

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
