package com.esferalia.aon.file.payroll.contrata;

import java.io.Serializable;
import java.util.Date;

import com.code.aon.AonVersion;

public class ContrataProrrogaParams implements IContrataParams, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	/**
	 * DATOS_GENERALESPRORROGATYPE
	 */
	private String claveContrato;
	private Date fechaInicio;
	private Date fechaFin;
	private Boolean indicadorConvCol;
	private Boolean indicadorDiscontinuidad;
	private Boolean indEmpresaAappUniversidad;
	private Boolean indPeriodoAutorizaDuracion;
	
	/**
	 * DATOS_ADICIONALESPRORROGATYPE
	 */
	private String horasFormacion;
	private String minutosFormacion;
	private Boolean indDuracInferior;
	
	/**
	 * DATOS_USOLIBRE_EMPRESATYPE
	 */
	private String usoLibreEmpresa;


	
	public String getClaveContrato() {
		return claveContrato;
	}

	public void setClaveContrato(String claveContrato) {
		this.claveContrato = claveContrato;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Boolean getIndicadorConvCol() {
		return indicadorConvCol;
	}

	public void setIndicadorConvCol(Boolean indicadorConvCol) {
		this.indicadorConvCol = indicadorConvCol;
	}

	public Boolean getIndicadorDiscontinuidad() {
		return indicadorDiscontinuidad;
	}

	public void setIndicadorDiscontinuidad(Boolean indicadorDiscontinuidad) {
		this.indicadorDiscontinuidad = indicadorDiscontinuidad;
	}

	public Boolean getIndEmpresaAappUniversidad() {
		return indEmpresaAappUniversidad;
	}

	public void setIndEmpresaAappUniversidad(Boolean indEmpresaAappUniversidad) {
		this.indEmpresaAappUniversidad = indEmpresaAappUniversidad;
	}

	public Boolean getIndPeriodoAutorizaDuracion() {
		return indPeriodoAutorizaDuracion;
	}

	public void setIndPeriodoAutorizaDuracion(Boolean indPeriodoAutorizaDuracion) {
		this.indPeriodoAutorizaDuracion = indPeriodoAutorizaDuracion;
	}

	public String getHorasFormacion() {
		return horasFormacion;
	}

	public void setHorasFormacion(String horasFormacion) {
		this.horasFormacion = horasFormacion;
	}

	public String getMinutosFormacion() {
		return minutosFormacion;
	}

	public void setMinutosFormacion(String minutosFormacion) {
		this.minutosFormacion = minutosFormacion;
	}

	public Boolean getIndDuracInferior() {
		return indDuracInferior;
	}

	public void setIndDuracInferior(Boolean indDuracInferior) {
		this.indDuracInferior = indDuracInferior;
	}

	public String getUsoLibreEmpresa() {
		return usoLibreEmpresa;
	}

	public void setUsoLibreEmpresa(String usoLibreEmpresa) {
		this.usoLibreEmpresa = usoLibreEmpresa;
	}
	
	
}
