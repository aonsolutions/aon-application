package com.code.aon.ui.loader.pojo;

import java.util.Date;

public class LoadedAmortization implements ILoadedPojo{

	private String codigo;
	private Date fechaInicio;
	private String descripcion;	
	private Integer periodo;
	private Double coeficiente;
	private Double importe;
	private String cuentaInm;
	private String cuentaAcu;
	private String cuentaDot;
	private String codigoActividad;
	
	
	@Override
	public String getIdentifier() {
		return getCodigo();
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Double getCoeficiente() {
		return coeficiente;
	}

	public void setCoeficiente(Double coeficiente) {
		this.coeficiente = coeficiente;
	}

	public Double getImporte() {
		return importe;
	}

	public void setImporte(Double importe) {
		this.importe = importe;
	}

	public String getCuentaInm() {
		return cuentaInm;
	}

	public void setCuentaInm(String cuentaInm) {
		this.cuentaInm = cuentaInm;
	}

	public String getCuentaAcu() {
		return cuentaAcu;
	}

	public void setCuentaAcu(String cuentaAcu) {
		this.cuentaAcu = cuentaAcu;
	}

	public String getCuentaDot() {
		return cuentaDot;
	}

	public void setCuentaDot(String cuentaDot) {
		this.cuentaDot = cuentaDot;
	}

	public String getCodigoActividad() {
		return codigoActividad;
	}

	public void setCodigoActividad(String codigoActividad) {
		this.codigoActividad = codigoActividad;
	}	
	
}
