package com.code.aon.ui.loader.pojo;

import java.util.Date;

public class LoadedAmortizationDetail implements ILoadedPojo{
	
	private String codigo;
	private Date fechaDesde;
	private Date fechaHasta;
	private Double coeficiente;
	private Double dotacionContable;
	private Double dotacionFiscal;
	private Integer estado;
	

	@Override
	public String getIdentifier() {
		return (getCodigo() + "-" + getFechaDesde());
	}
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public Date getFechaDesde() {
		return fechaDesde;
	}
	public void setFechaDesde(Date fechaDesde) {
		this.fechaDesde = fechaDesde;
	}
	public Date getFechaHasta() {
		return fechaHasta;
	}
	public void setFechaHasta(Date fechaHasta) {
		this.fechaHasta = fechaHasta;
	}
	public Double getCoeficiente() {
		return coeficiente;
	}
	public void setCoeficiente(Double coeficiente) {
		this.coeficiente = coeficiente;
	}
	public Double getDotacionContable() {
		return dotacionContable;
	}
	public void setDotacionContable(Double dotacionContable) {
		this.dotacionContable = dotacionContable;
	}
	public Double getDotacionFiscal() {
		return dotacionFiscal;
	}
	public void setDotacionFiscal(Double dotacionFiscal) {
		this.dotacionFiscal = dotacionFiscal;
	}
	public Integer getEstado() {
		return estado;
	}
	public void setEstado(Integer estado) {
		this.estado = estado;
	}
	
}
