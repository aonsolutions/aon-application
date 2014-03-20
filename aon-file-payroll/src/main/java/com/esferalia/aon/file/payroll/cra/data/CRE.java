package com.esferalia.aon.file.payroll.cra.data;

/**
 * 
 * CRE - Conceptos REtribuidos
 * 
 */
public class CRE {
	
	private String concepto;
	private String indicativoConcepto;
	private String importe;
	private String indicativoTipoActuacion;
	
	
	public String getConcepto() {
		return concepto;
	}
	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}
	public String getIndicativoConcepto() {
		return indicativoConcepto;
	}
	public void setIndicativoConcepto(String indicativoConcepto) {
		this.indicativoConcepto = indicativoConcepto;
	}
	public String getImporte() {
		return importe;
	}
	public void setImporte(String importe) {
		this.importe = importe;
	}
	public String getIndicativoTipoActuacion() {
		return indicativoTipoActuacion;
	}
	public void setIndicativoTipoActuacion(String indicativoTipoActuacion) {
		this.indicativoTipoActuacion = indicativoTipoActuacion;
	}
	
}
