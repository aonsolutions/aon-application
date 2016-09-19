package com.esferalia.aon.file.payroll.contrata;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.person.Person;
import com.esferalia.aon.payroll.CNO;
import com.esferalia.aon.payroll.enumeration.ContractCode;
import com.esferalia.aon.payroll.enumeration.contrata.TEJINDIS;
import com.esferalia.aon.payroll.enumeration.contrata.TELCOLBO;
import com.esferalia.aon.payroll.enumeration.contrata.TEOCOLDE;
import com.esferalia.aon.payroll.enumeration.contrata.TEQPTIEM;
import com.esferalia.aon.payroll.enumeration.contrata.TERFIRCB;
import com.esferalia.aon.sepe.api.contract.model.ITransformacionType;


public class ContrataTransformacionesParams implements IContrataParams, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String sourceContractSepeId;
	private ContractCode sourceContractCode;
	private ITransformacionType transformContract;
//	private ContractCode transformCode;
	private String transformCode;
	private Date fechaInicio;
	private Date sourceStartDate;
	private Date fechaTerminoReal;
	private String indicadorDiscontinuidad;

	/* 
	 * generales de contrato 
	 */
	private CNO cno;
	
	/* 
	 * datos de contrata 
	 */
	private String usoLibreEmpresa;

	private boolean indCosteDespido;
	private TEOCOLDE codigoColectivoDespido;

//	DATOS_GENERALESCONTRATOTYPE
	private TEJINDIS indDiscapacidad;
	
//	DATOSCOMUNICACOPIABASICATYPE
	private TERFIRCB tipoFirmaCopiaBasica;
	private String textoCopiaBasica;

//	// CONTRATO RELEVO
	private Person reliefPerson;
	
	///////////////////////////////////////////////////
	// checks datos especificos contrato
	///////////////////////////////////////////////////
	private boolean reliefData;
	private boolean datosBonificacionData;

	private boolean showReliefData;
	private boolean showColectivoBonificacion;
	
//	DATOSCONTRATOTIEMPOPARCIALTYPE
	private TEQPTIEM tipoJornada;
	private String horasJornada;
	private String minutosJornada;
	private String horasConvenio;
	private String minutosConvenio;
	private String actividadSinFechaCierta;
	private Boolean fijoDiscontinuoPeriodico;
	
//	DATOS_BONIFICACIONTYPE
	private TELCOLBO colectivoBonificacion;
	
	
	public String getSourceContractSepeId() {
		return sourceContractSepeId;
	}

	public void setSourceContractSepeId(String sourceContractSepeId) {
		this.sourceContractSepeId = sourceContractSepeId;
	}

	public ContractCode getSourceContractCode() {
		return sourceContractCode;
	}

	public void setSourceContractCode(ContractCode sourceContractCode) {
		this.sourceContractCode = sourceContractCode;
	}

	public String getIndicadorDiscontinuidad() {
		return indicadorDiscontinuidad;
	}

	public void setIndicadorDiscontinuidad(String indicadorDiscontinuidad) {
		this.indicadorDiscontinuidad = indicadorDiscontinuidad;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getSourceStartDate() {
		return sourceStartDate;
	}

	public void setSourceStartDate(Date sourceStartDate) {
		this.sourceStartDate = sourceStartDate;
	}

	public Date getFechaTerminoReal() {
		return fechaTerminoReal;
	}

	public void setFechaTerminoReal(Date fechaTerminoReal) {
		this.fechaTerminoReal = fechaTerminoReal;
	}

	public ITransformacionType getTransformContract() {
		return transformContract;
	}

	public void setTransformContract(ITransformacionType transformContract) {
		this.transformContract = transformContract;
	}

//	public ContractCode getTransformCode() {
//		return transformCode;
//	}
//
//	public void setTransformCode(ContractCode transformCode) {
//		this.transformCode = transformCode;
//	}

	public String getTransformCode() {
		return transformCode;
	}

	public void setTransformCode(String transformCode) {
		this.transformCode = transformCode;
	}


	public boolean isShowReliefData() {
		return showReliefData;
	}
	public void setShowReliefData(boolean showReliefData) {
		this.showReliefData = showReliefData;
	}
	
	public boolean isDatosBonificacionData() {
		return datosBonificacionData;
	}

	public void setDatosBonificacionData(boolean datosBonificacionData) {
		this.datosBonificacionData = datosBonificacionData;
	}

	public boolean isShowColectivoBonificacion() {
		return showColectivoBonificacion;
	}

	public void setShowColectivoBonificacion(boolean showColectivoBonificacion) {
		this.showColectivoBonificacion = showColectivoBonificacion;
	}

	public boolean isReliefData() {
		return reliefData;
	}
	public void setReliefData(boolean reliefData) {
		this.reliefData = reliefData;
	}

	public TEJINDIS getIndDiscapacidad() {
		return indDiscapacidad;
	}
	public void setIndDiscapacidad(TEJINDIS indDiscapacidad) {
		this.indDiscapacidad = indDiscapacidad;
	}
	public TELCOLBO getColectivoBonificacion() {
		return colectivoBonificacion;
	}
	public void setColectivoBonificacion(TELCOLBO colectivoBonificacion) {
		this.colectivoBonificacion = colectivoBonificacion;
	}
	
	public CNO getCno() {
		if(cno==null){
			cno = new CNO();
		}
		return cno;
	}
	public void setCno(CNO cno) {
		this.cno = cno;
	}

	public Person getReliefPerson() {
		if(reliefPerson==null){
			reliefPerson = new Person();
		}
		return reliefPerson;
	}
	public void setReliefPerson(Person reliefPerson) {
		this.reliefPerson = reliefPerson;
	}
	
	public TEOCOLDE getCodigoColectivoDespido() {
		return codigoColectivoDespido;
	}
	public void setCodigoColectivoDespido(TEOCOLDE codigoColectivoDespido) {
		this.codigoColectivoDespido = codigoColectivoDespido;
	}
	public boolean isIndCosteDespido() {
		return indCosteDespido;
	}
	public void setIndCosteDespido(boolean indCosteDespido) {
		this.indCosteDespido = indCosteDespido;
	}
	public String getUsoLibreEmpresa() {
		return usoLibreEmpresa;
	}
	public void setUsoLibreEmpresa(String usoLibreEmpresa) {
		this.usoLibreEmpresa = usoLibreEmpresa;
	}
	public TERFIRCB getTipoFirmaCopiaBasica() {
		return tipoFirmaCopiaBasica;
	}
	public void setTipoFirmaCopiaBasica(TERFIRCB tipoFirmaCopiaBasica) {
		this.tipoFirmaCopiaBasica = tipoFirmaCopiaBasica;
	}
	public String getTextoCopiaBasica() {
		return textoCopiaBasica;
	}
	public void setTextoCopiaBasica(String textoCopiaBasica) {
		this.textoCopiaBasica = textoCopiaBasica;
	}

	public TEQPTIEM getTipoJornada() {
		return tipoJornada;
	}
	public void setTipoJornada(TEQPTIEM tipoJornada) {
		this.tipoJornada = tipoJornada;
	}
	public String getHorasJornada() {
		return horasJornada;
	}
	public void setHorasJornada(String horasJornada) {
		this.horasJornada = horasJornada;
	}
	public String getMinutosJornada() {
		return minutosJornada;
	}
	public void setMinutosJornada(String minutosJornada) {
		this.minutosJornada = minutosJornada;
	}
	public String getHorasConvenio() {
		return horasConvenio;
	}
	public void setHorasConvenio(String horasConvenio) {
		this.horasConvenio = horasConvenio;
	}
	public String getMinutosConvenio() {
		return minutosConvenio;
	}
	public void setMinutosConvenio(String minutosConvenio) {
		this.minutosConvenio = minutosConvenio;
	}
	public String getActividadSinFechaCierta() {
		return actividadSinFechaCierta;
	}
	public void setActividadSinFechaCierta(String actividadSinFechaCierta) {
		this.actividadSinFechaCierta = actividadSinFechaCierta;
	}
	public Boolean getFijoDiscontinuoPeriodico() {
		return fijoDiscontinuoPeriodico;
	}
	public void setFijoDiscontinuoPeriodico(Boolean fijoDiscontinuoPeriodico) {
		this.fijoDiscontinuoPeriodico = fijoDiscontinuoPeriodico;
	}

	
}
