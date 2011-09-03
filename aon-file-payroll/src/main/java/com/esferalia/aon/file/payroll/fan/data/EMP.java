package com.esferalia.aon.file.payroll.fan.data;

import java.util.LinkedList;
import java.util.List;

/**
 * Identificacion de empresa
 * 
 */
public class EMP {
	private String codigoCuentaCotizacionSeguridadSocial;
	private String tipo;
	private String pais;
	private String numero;
	private String calificador;
	private String codigoCuentaCotizacionPrincipal;
	private Integer anio;
	private Integer desdeMes;
	private Integer hastaMes;
	private String calificadorLiquidacion;
	private Integer claseLiquidacion;
	
	private RZS rzs;
	private EXC exc;
	
	private List<TRA> trabajadores;
	
	public String getCodigoCuentaCotizacionSeguridadSocial() {
		return codigoCuentaCotizacionSeguridadSocial;
	}
	public void setCodigoCuentaCotizacionSeguridadSocial(String codigoCuentaCotizacionSeguridadSocial) {
		this.codigoCuentaCotizacionSeguridadSocial = codigoCuentaCotizacionSeguridadSocial;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getPais() {
		return pais;
	}
	public void setPais(String pais) {
		this.pais = pais;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
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
	public Integer getAnio() {
		return anio;
	}
	public void setAnio(Integer anio) {
		this.anio = anio;
	}
	public Integer getDesdeMes() {
		return desdeMes;
	}
	public void setDesdeMes(Integer desdeMes) {
		this.desdeMes = desdeMes;
	}
	public Integer getHastaMes() {
		return hastaMes;
	}
	public void setHastaMes(Integer hastaMes) {
		this.hastaMes = hastaMes;
	}
	public String getCalificadorLiquidacion() {
		return calificadorLiquidacion;
	}
	public void setCalificadorLiquidacion(String calificadorLiquidacion) {
		this.calificadorLiquidacion = calificadorLiquidacion;
	}
	public Integer getClaseLiquidacion() {
		return claseLiquidacion;
	}
	public void setClaseLiquidacion(Integer claseLiquidacion) {
		this.claseLiquidacion = claseLiquidacion;
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
