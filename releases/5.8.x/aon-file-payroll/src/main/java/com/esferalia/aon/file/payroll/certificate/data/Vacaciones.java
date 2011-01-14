package com.esferalia.aon.file.payroll.certificate.data;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Vacaciones {
	
	private static final String DATOS_VACACIONES_COTIZADAS = "Datos_VacacionesCotizadas";
	private static final String NUM_DIAS_COTIZADOS = "NumDiasCotizados";
	private static final String BASE_COTIZACION_DESEMPLEO = "BaseCotizacionDesempleo";
	private static final String BASE_COTIZACION_CONTINGENCIAS_COMUNES = "BaseCotizacionContingenciasComunes";
	private static final String OBSERVACIONES = "Observaciones";
	
	private String numDiasCotizados;
	private String baseCotizacionDesempleo;
	private String baseCotizacionContingenciasComunes;
	private String observaciones;
	
	public String getNumDiasCotizados() {
		return numDiasCotizados;
	}
	public void setNumDiasCotizados(String numDiasCotizados) {
		this.numDiasCotizados = numDiasCotizados;
	}
	public String getBaseCotizacionDesempleo() {
		return baseCotizacionDesempleo;
	}
	public void setBaseCotizacionDesempleo(String baseCotizacionDesempleo) {
		this.baseCotizacionDesempleo = baseCotizacionDesempleo;
	}
	public String getBaseCotizacionContingenciasComunes() {
		return baseCotizacionContingenciasComunes;
	}
	public void setBaseCotizacionContingenciasComunes(String baseCotizacionContingenciasComunes) {
		this.baseCotizacionContingenciasComunes = baseCotizacionContingenciasComunes;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public Element getElement(Document xmldoc) {
		Element datosVacacionesCotizadas = xmldoc.createElement(DATOS_VACACIONES_COTIZADAS);

		Element numDiasCotizados = xmldoc.createElement(NUM_DIAS_COTIZADOS);
		numDiasCotizados.appendChild(xmldoc.createTextNode(getNumDiasCotizados().toString()));
		datosVacacionesCotizadas.appendChild(numDiasCotizados);

		Element baseCotizacionContingenciasComunes = xmldoc.createElement(BASE_COTIZACION_CONTINGENCIAS_COMUNES);
		baseCotizacionContingenciasComunes.appendChild(xmldoc.createTextNode(getBaseCotizacionContingenciasComunes().toString()));
		datosVacacionesCotizadas.appendChild(baseCotizacionContingenciasComunes);

		Element baseCotizacionDesempleo = xmldoc.createElement(BASE_COTIZACION_DESEMPLEO);
		baseCotizacionDesempleo.appendChild(xmldoc.createTextNode(getBaseCotizacionDesempleo().toString()));
		datosVacacionesCotizadas.appendChild(baseCotizacionDesempleo);

		Element observaciones = null;
		if(!StringUtils.isBlank(getObservaciones())){
			observaciones = xmldoc.createElement(OBSERVACIONES);
			observaciones.appendChild(xmldoc.createTextNode(getObservaciones()));
			datosVacacionesCotizadas.appendChild(observaciones);
		}
		
		return datosVacacionesCotizadas;
	}
	
}
