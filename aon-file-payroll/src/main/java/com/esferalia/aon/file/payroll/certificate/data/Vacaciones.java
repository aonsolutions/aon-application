package com.esferalia.aon.file.payroll.certificate.data;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Vacaciones {
	
	private static final String DATOS_VACACIONES_COTIZADAS = "Datos_VacacionesCotizadas";
	private static final String NUM_DIAS_COTIZADOS = "NumDiasCotizados";
	private static final String BASE_COTIZACION_DESEMPLEO = "BaseCotizacionDesempleo";
	private static final String BASE_COTIZACION_CONTINGENCIAS_COMUNES = "BaseCotizacionContingenciasComunes";
	private static final String OBSERVACIONES = "Observaciones";
	
	private Integer numDiasCotizados;
	private Integer baseCotizacionDesempleo;
	private Integer baseCotizacionContingenciasComunes;
	private String observaciones;
	
	public Integer getNumDiasCotizados() {
		return numDiasCotizados;
	}
	public void setNumDiasCotizados(Integer numDiasCotizados) {
		this.numDiasCotizados = numDiasCotizados;
	}
	public Integer getBaseCotizacionDesempleo() {
		return baseCotizacionDesempleo;
	}
	public void setBaseCotizacionDesempleo(Integer baseCotizacionDesempleo) {
		this.baseCotizacionDesempleo = baseCotizacionDesempleo;
	}
	public Integer getBaseCotizacionContingenciasComunes() {
		return baseCotizacionContingenciasComunes;
	}
	public void setBaseCotizacionContingenciasComunes(Integer baseCotizacionContingenciasComunes) {
		this.baseCotizacionContingenciasComunes = baseCotizacionContingenciasComunes;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public Element getElement(Document xmldoc) {
		Element numDiasCotizados = xmldoc.createElement(NUM_DIAS_COTIZADOS);
		Element baseCotizacionDesempleo = xmldoc.createElement(BASE_COTIZACION_DESEMPLEO);
		Element baseCotizacionContingenciasComunes = xmldoc.createElement(BASE_COTIZACION_CONTINGENCIAS_COMUNES);
		Element observaciones = xmldoc.createElement(OBSERVACIONES);
		
		numDiasCotizados.appendChild(xmldoc.createTextNode(getNumDiasCotizados().toString()));
		baseCotizacionDesempleo.appendChild(xmldoc.createTextNode(getBaseCotizacionDesempleo().toString()));
		baseCotizacionContingenciasComunes.appendChild(xmldoc.createTextNode(getBaseCotizacionContingenciasComunes().toString()));
		observaciones.appendChild(xmldoc.createTextNode(getObservaciones()));
		
		Element datosVacacionesCotizadas = xmldoc.createElement(DATOS_VACACIONES_COTIZADAS);
		datosVacacionesCotizadas.appendChild(numDiasCotizados);
		datosVacacionesCotizadas.appendChild(baseCotizacionDesempleo);
		datosVacacionesCotizadas.appendChild(baseCotizacionContingenciasComunes);
		datosVacacionesCotizadas.appendChild(observaciones);
		
		return datosVacacionesCotizadas;
	}
	
}
