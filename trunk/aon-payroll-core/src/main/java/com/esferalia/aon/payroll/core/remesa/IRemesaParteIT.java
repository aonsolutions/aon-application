package com.esferalia.aon.payroll.core.remesa;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.enumeration.Periodicidad;
import com.esferalia.aon.payroll.core.enumeration.TipoContingencia;
import com.esferalia.aon.payroll.core.enumeration.TipoOperacionIT;

public interface IRemesaParteIT extends Serializable {

	Integer getId();
	void setId(Integer id);
	
	IRemesaINSS getRemesaINSS();
	void setRemesaINSS(IRemesaINSS remesaINSS);
	
	IEmpleado getEmpleado();
	void setEmpleado(IEmpleado empleado);

	Date getFechaBaja();
	void setFechaBaja(Date fechaBaja);

	Date getFechaParte();
	void setFechaParte(Date fechaParte);
	
	String getNumeroColegiado();
	void setNumeroColegiado(String numeroColegiadoBaja);

	String getCias();
	void setCias(String ciasBaja);

	TipoOperacionIT getTipoOperacionIT();
	void setTipoOperacionIT(TipoOperacionIT tipoOperacionIT);
	
	Integer getNumero();
	void setNumero(Integer numero);
	
	TipoContingencia getTipoContingencia();
	void setTipoContingencia(TipoContingencia tipoContingencia);
	
	boolean isRecaida();
	void setRecaida(boolean recaida);
	
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
