package com.esferalia.aon.file.payroll.cra.data;

import java.util.LinkedList;
import java.util.List;

/**
 * Identificacion de empresa
 * 
 */
public class DDE {
	
	
	private String codigoCuentaCotizacionSeguridadSocial;
	private String anio;
	private String mes;
	
	private List<TRB> trbList;

	
	public String getCodigoCuentaCotizacionSeguridadSocial() {
		return codigoCuentaCotizacionSeguridadSocial;
	}

	public void setCodigoCuentaCotizacionSeguridadSocial(
			String codigoCuentaCotizacionSeguridadSocial) {
		this.codigoCuentaCotizacionSeguridadSocial = codigoCuentaCotizacionSeguridadSocial;
	}

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

	public List<TRB> getTrbList() {
		if(trbList==null){
			trbList = new LinkedList<TRB>();
		}
		return trbList;
	}

	public void setTrbList(List<TRB> trbList) {
		this.trbList = trbList;
	}
	
	
}
