package com.code.aon.ui.audabridge.response;

public class Operacion {
	private String tipoLinea;
	private String numeroOperacion;
	private String descripcion;
	private double numeroUt;
	private String cambioUt;
	private String cambioImporte;
	private double importe;
	public String getTipoLinea() {
		return tipoLinea;
	}
	public void setTipoLinea(String tipoLinea) {
		this.tipoLinea = tipoLinea;
	}
	public String getNumeroOperacion() {
		return numeroOperacion;
	}
	public void setNumeroOperacion(String numeroOperacion) {
		this.numeroOperacion = numeroOperacion;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public double getNumeroUt() {
		return numeroUt;
	}
	public void setNumeroUt(double numeroUt) {
		this.numeroUt = numeroUt;
	}
	public String getCambioUt() {
		return cambioUt;
	}
	public void setCambioUt(String cambioUt) {
		this.cambioUt = cambioUt;
	}
	public String getCambioImporte() {
		return cambioImporte;
	}
	public void setCambioImporte(String cambioImporte) {
		this.cambioImporte = cambioImporte;
	}
	public double getImporte() {
		return importe;
	}
	public void setImporte(double importe) {
		this.importe = importe;
	}

	
}
