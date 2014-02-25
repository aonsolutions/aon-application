package com.esferalia.aon.file.payroll.fan.data;

import java.util.Map;
import java.util.TreeMap;

/**
 * datos de trabajador y periodo
 */
public class DAT {
	
	private Integer mes;
	private String indicadoresPerfil;
	private Integer diasHoras;
	private Integer diasAlta;
	private String indicadorCotizacion;
	private String indicadorVacaciones;
	private String claveJornadasColectivo;
	private Integer especificos;
	private Integer indReduccionBoni;
	private Integer grupoCotizacion;
	private String tipoContrato;
	private String claveContrato;
	private Integer epigrafeAtEp;
	private Integer epigrafeSecundario;
	private String ocupacion;
	private String modalidadCotizacion;
	private String indDiscapacidad;
	private Integer indRelacion;
	private String colectivoPeculiar;
	private String infoComplementaria;
	private String cotizacionDesempleo;
	
	private Map<String, EDL> edl;

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public String getIndicadoresPerfil() {
		return indicadoresPerfil;
	}

	public void setIndicadoresPerfil(String indicadoresPerfil) {
		this.indicadoresPerfil = indicadoresPerfil;
	}

	public Integer getDiasHoras() {
		return diasHoras;
	}

	public void setDiasHoras(Integer diasHoras) {
		this.diasHoras = diasHoras;
	}

	public Integer getDiasAlta() {
		return diasAlta;
	}

	public void setDiasAlta(Integer diasAlta) {
		this.diasAlta = diasAlta;
	}

	public String getIndicadorCotizacion() {
		return indicadorCotizacion;
	}

	public void setIndicadorCotizacion(String indicadorCotizacion) {
		this.indicadorCotizacion = indicadorCotizacion;
	}

	public String getIndicadorVacaciones() {
		return indicadorVacaciones;
	}

	public void setIndicadorVacaciones(String indicadorVacaciones) {
		this.indicadorVacaciones = indicadorVacaciones;
	}

	public String getClaveJornadasColectivo() {
		return claveJornadasColectivo;
	}

	public void setClaveJornadasColectivo(String claveJornadasColectivo) {
		this.claveJornadasColectivo = claveJornadasColectivo;
	}

	public Integer getEspecificos() {
		return especificos;
	}

	public void setEspecificos(Integer especificos) {
		this.especificos = especificos;
	}

	public Integer getIndReduccionBoni() {
		return indReduccionBoni;
	}

	public void setIndReduccionBoni(Integer indReduccionBoni) {
		this.indReduccionBoni = indReduccionBoni;
	}

	public Integer getGrupoCotizacion() {
		return grupoCotizacion;
	}

	public void setGrupoCotizacion(Integer grupoCotizacion) {
		this.grupoCotizacion = grupoCotizacion;
	}

	public String getTipoContrato() {
		return tipoContrato;
	}

	public void setTipoContrato(String tipoContrato) {
		this.tipoContrato = tipoContrato;
	}

	public String getClaveContrato() {
		return claveContrato;
	}

	public void setClaveContrato(String claveContrato) {
		this.claveContrato = claveContrato;
	}

	public Integer getEpigrafeAtEp() {
		return epigrafeAtEp;
	}

	public void setEpigrafeAtEp(Integer epigrafeAtEp) {
		this.epigrafeAtEp = epigrafeAtEp;
	}

	public Integer getEpigrafeSecundario() {
		return epigrafeSecundario;
	}

	public void setEpigrafeSecundario(Integer epigrafeSecundario) {
		this.epigrafeSecundario = epigrafeSecundario;
	}

	public String getOcupacion() {
		return ocupacion;
	}

	public void setOcupacion(String ocupacion) {
		this.ocupacion = ocupacion;
	}

	public String getModalidadCotizacion() {
		return modalidadCotizacion;
	}

	public void setModalidadCotizacion(String modalidadCotizacion) {
		this.modalidadCotizacion = modalidadCotizacion;
	}

	public String getIndDiscapacidad() {
		return indDiscapacidad;
	}

	public void setIndDiscapacidad(String indDiscapacidad) {
		this.indDiscapacidad = indDiscapacidad;
	}

	public Integer getIndRelacion() {
		return indRelacion;
	}

	public void setIndRelacion(Integer indRelacion) {
		this.indRelacion = indRelacion;
	}

	public String getColectivoPeculiar() {
		return colectivoPeculiar;
	}

	public void setColectivoPeculiar(String colectivoPeculiar) {
		this.colectivoPeculiar = colectivoPeculiar;
	}

	public String getInfoComplementaria() {
		return infoComplementaria;
	}

	public void setInfoComplementaria(String infoComplementaria) {
		this.infoComplementaria = infoComplementaria;
	}

	public String getCotizacionDesempleo() {
		return cotizacionDesempleo;
	}
	
	public void setCotizacionDesempleo(String cotizacionDesempleo) {
		this.cotizacionDesempleo = cotizacionDesempleo;
	}
	
	public Map<String, EDL> getEdl() {
		if(edl==null){
			edl = new TreeMap<String, EDL>();
		}
		return edl;
	}
	public void setEdl(Map<String, EDL> edl) {
		this.edl = edl;
	}
	
	public EDL getEdlSegment(String key){
		if(getEdl().get(key)==null){
			getEdl().put(key, new EDL());
		}
		return getEdl().get(key);
	}
	
	
	
}
