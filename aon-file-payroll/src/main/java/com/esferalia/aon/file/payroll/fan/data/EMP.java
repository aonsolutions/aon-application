package com.esferalia.aon.file.payroll.fan.data;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
	private Integer anio;
	private Integer desdeMes;
	private Integer hastaMes;
	private String calificadorLiquidacion;
	private Integer claseLiquidacion;
	
	private RZS rzs;
	private EXC exc;
	
	private List<TRA> trabajadores;
	private List<TCT> tcTotales;
	private Map<String,EDT> edt;

	private MPG mpg;
//	private TYF tyf;
	
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
	
	public List<TCT> getTcTotales() {
		if(tcTotales==null){
			tcTotales = new LinkedList<TCT>();
		}
		return tcTotales;
	}
	public void setTcTotales(List<TCT> tcTotales) {
		this.tcTotales = tcTotales;
	}
	
	public Map<String, EDT> getEdt() {
		if(edt==null){
			edt = new TreeMap<String, EDT>();
		}
		return edt;
	}
	public void setEdt(Map<String, EDT> edt) {
		this.edt = edt;
	}
	
	public EDT getEdtSegment(String key){
		if(getEdt().get(key)==null){
			getEdt().put(key, new EDT());
		}
		return getEdt().get(key);
	}

	public MPG getMpg() {
		return mpg;
	}
	public void setMpg(MPG mpg) {
		this.mpg = mpg;
	}
	
	
}
