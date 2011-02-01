package com.esferalia.aon.file.payroll.afi.data;

/**
 * datos de subcontratacion o cesion
 */
public class DSC {
	
	private String tipo;
	private String cccCesion;
	private String nssCesion;

	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getCccCesion() {
		return cccCesion;
	}
	public void setCccCesion(String cccCesion) {
		this.cccCesion = cccCesion;
	}
	public String getNssCesion() {
		return nssCesion;
	}
	public void setNssCesion(String nssCesion) {
		this.nssCesion = nssCesion;
	}
	
}
