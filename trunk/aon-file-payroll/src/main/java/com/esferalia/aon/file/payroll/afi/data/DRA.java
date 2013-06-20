package com.esferalia.aon.file.payroll.afi.data;

/**
 * datos regimen especial agrario
 */
public class DRA {
	
	private String anio;
	private String mes;
	private String dia;
	private String fechaRealConsolidada;
	private String nuevaFechaReal;

	public String getAnio() {
		return anio;
	}
	public void setAnio(String anio) {
		this.anio = anio;
	}
	public String getMes() {
		return mes;
	}
	public void setMes(String mes) {
		this.mes = mes;
	}
	public String getDia() {
		return dia;
	}
	public void setDia(String dia) {
		this.dia = dia;
	}
	public String getFechaRealConsolidada() {
		return fechaRealConsolidada;
	}
	public void setFechaRealConsolidada(String fechaRealConsolidada) {
		this.fechaRealConsolidada = fechaRealConsolidada;
	}
	public String getNuevaFechaReal() {
		return nuevaFechaReal;
	}
	public void setNuevaFechaReal(String nuevaFechaReal) {
		this.nuevaFechaReal = nuevaFechaReal;
	}

}
