package com.esferalia.aon.file.payroll.afi.data;

import java.util.LinkedList;
import java.util.List;

/**
 * Identificacion de empresa
 * 
 */
public class EMP {
	private String codigoCuentaCotizacionSeguridadSocial;
	private String tipoDocumento;
	private String pais;
	private String numeroIdentificacion;
	private String calificador;
	private String codigoCuentaCotizacionPrincipal;
	private String accion;

	private RZS rzs;
	private EXC exc;
	private FCE fce;
	private List<TRA> trabajadores;
	
	public String getCodigoCuentaCotizacionSeguridadSocial() {
		return codigoCuentaCotizacionSeguridadSocial;
	}
	public void setCodigoCuentaCotizacionSeguridadSocial(String codigoCuentaCotizacionSeguridadSocial) {
		this.codigoCuentaCotizacionSeguridadSocial = codigoCuentaCotizacionSeguridadSocial;
	}
	public String getTipoDocumento() {
		return tipoDocumento;
	}
	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getNumeroIdentificacion() {
		return numeroIdentificacion;
	}
	public void setNumeroIdentificacion(String numeroIdentificacion) {
		this.numeroIdentificacion = numeroIdentificacion;
	}
	public String getCalificador() {
		return calificador;
	}
	public void setCalificador(String calificador) {
		this.calificador = calificador;
	}
	public String getCodigoCuentaCotizacionPrincipal() {
		return codigoCuentaCotizacionPrincipal;
	}
	public void setCodigoCuentaCotizacionPrincipal(String codigoCuentaCotizacionPrincipal) {
		this.codigoCuentaCotizacionPrincipal = codigoCuentaCotizacionPrincipal;
	}
	public String getAccion() {
		return accion;
	}
	public void setAccion(String accion) {
		this.accion = accion;
	}
	public RZS getRzs() {
		return rzs;
	}
	public void setRzs(RZS rzs) {
		this.rzs = rzs;
	}
	public EXC getExc() {
		return exc;
	}
	public void setExc(EXC exc) {
		this.exc = exc;
	}
	public FCE getFce() {
		return fce;
	}
	public void setFce(FCE fce) {
		this.fce = fce;
	}

	public List<TRA> getTrabajadores() {
		if (trabajadores == null) {
			trabajadores = new LinkedList<TRA>();
		}
		return trabajadores;
	}
	public void setTrabajadores(List<TRA> trabajadores) {
		this.trabajadores = trabajadores;
	}
}
