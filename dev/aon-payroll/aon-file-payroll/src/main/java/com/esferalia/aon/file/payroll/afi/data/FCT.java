package com.esferalia.aon.file.payroll.afi.data;

/**
 * fechas de control de trabajadores
 */
public class FCT {
	
	private String tipoSituacionAdicionalAfiliacion;
	private String fechaFinVacaciones;
	private String fechaDesde;
	private String fechaHasta;
	private String fechaDesdeNueva;
	private String fechaHastaNueva;
	private String fechaPresentacion;
	private String fechaEfectoAlta;

	public String getTipoSituacionAdicionalAfiliacion() {
		return tipoSituacionAdicionalAfiliacion;
	}
	public void setTipoSituacionAdicionalAfiliacion(
			String tipoSituacionAdicionalAfiliacion) {
		this.tipoSituacionAdicionalAfiliacion = tipoSituacionAdicionalAfiliacion;
	}
	public String getFechaFinVacaciones() {
		return fechaFinVacaciones;
	}
	public void setFechaFinVacaciones(String fechaFinVacaciones) {
		this.fechaFinVacaciones = fechaFinVacaciones;
	}
	public String getFechaDesde() {
		return fechaDesde;
	}
	public void setFechaDesde(String fechaDesde) {
		this.fechaDesde = fechaDesde;
	}
	public String getFechaHasta() {
		return fechaHasta;
	}
	public void setFechaHasta(String fechaHasta) {
		this.fechaHasta = fechaHasta;
	}
	public String getFechaDesdeNueva() {
		return fechaDesdeNueva;
	}
	public void setFechaDesdeNueva(String fechaDesdeNueva) {
		this.fechaDesdeNueva = fechaDesdeNueva;
	}
	public String getFechaHastaNueva() {
		return fechaHastaNueva;
	}
	public void setFechaHastaNueva(String fechaHastaNueva) {
		this.fechaHastaNueva = fechaHastaNueva;
	}
	public String getFechaPresentacion() {
		return fechaPresentacion;
	}
	public void setFechaPresentacion(String fechaPresentacion) {
		this.fechaPresentacion = fechaPresentacion;
	}
	public String getFechaEfectoAlta() {
		return fechaEfectoAlta;
	}
	public void setFechaEfectoAlta(String fechaEfectoAlta) {
		this.fechaEfectoAlta = fechaEfectoAlta;
	}

}
