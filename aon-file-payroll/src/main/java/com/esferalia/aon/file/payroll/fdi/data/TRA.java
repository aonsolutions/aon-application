package com.esferalia.aon.file.payroll.fdi.data;


public class TRA {
	private String numeroAfiliacion;
	private String ipf;
	private DOM dom;
	private LDD ldd;
	private DIT datosIT;
	
	public String getNumeroAfiliacion() {
		return numeroAfiliacion;
	}
	public void setNumeroAfiliacion(String numeroAfiliacion) {
		this.numeroAfiliacion = numeroAfiliacion;
	}
	
	public String getIpf() {
		return ipf;
	}
	public void setIpf(String ipf) {
		this.ipf = ipf;
	}
	
	public DOM getDom() {
		return dom;
	}
	public void setDom(DOM dom) {
		this.dom = dom;
	}
	public LDD getLdd() {
		return ldd;
	}
	public void setLdd(LDD ldd) {
		this.ldd = ldd;
	}
	public DIT getDatosIT() {
		return datosIT;
	}
	public void setDatosIT(DIT datosIT) {
		this.datosIT = datosIT;
	}
}
