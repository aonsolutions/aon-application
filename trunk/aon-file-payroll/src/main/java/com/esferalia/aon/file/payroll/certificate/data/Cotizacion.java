package com.esferalia.aon.file.payroll.certificate.data;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Cotizacion {
	
	private static final String DATOS_COTIZACION = "Datos_Cotizacion";
	private static final String ANO = "Ano";
	private static final String MES = "Mes";
	private static final String NUM_DIAS_COTIZADOS = "NumDiasCotizados";
	private static final String BASE_COTIZACION_CONTINGENCIAS_COMUNES = "BaseCotizacionContingenciasComunes";
	private static final String BASE_COTIZACION_DESEMPLEO = "BaseCotizacionDesempleo";
	private static final String OBSERVACIONES = "Observaciones";

	private Integer ano;
	private Integer mes;
	private Integer numDiasCotizados;
	private Integer baseCotizacionContingenciasComunes;
	private Integer baseCotizacionDesempleo;
	private String observaciones;

	public Integer getAno() {
		return ano;
	}
	public void setAno(Integer ano) {
		this.ano = ano;
	}
	public Integer getMes() {
		return mes;
	}
	public void setMes(Integer mes) {
		this.mes = mes;
	}
	public Integer getNumDiasCotizados() {
		return numDiasCotizados;
	}
	public void setNumDiasCotizados(Integer numDiasCotizados) {
		this.numDiasCotizados = numDiasCotizados;
	}
	public Integer getBaseCotizacionContingenciasComunes() {
		return baseCotizacionContingenciasComunes;
	}
	public void setBaseCotizacionContingenciasComunes(Integer baseCotizacionContingenciasComunes) {
		this.baseCotizacionContingenciasComunes = baseCotizacionContingenciasComunes;
	}
	public Integer getBaseCotizacionDesempleo() {
		return baseCotizacionDesempleo;
	}
	public void setBaseCotizacionDesempleo(Integer baseCotizacionDesempleo) {
		this.baseCotizacionDesempleo = baseCotizacionDesempleo;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public Node getElement(Document xmldoc) {
		Element ano = xmldoc.createElement(ANO);
		Element mes = xmldoc.createElement(MES);
		Element numDiasCotizados = xmldoc.createElement(NUM_DIAS_COTIZADOS);
		Element baseCotizacionContingenciasComunes = xmldoc.createElement(BASE_COTIZACION_CONTINGENCIAS_COMUNES);
		Element baseCotizacionDesempleo = xmldoc.createElement(BASE_COTIZACION_DESEMPLEO);
		Element observaciones = xmldoc.createElement(OBSERVACIONES);
		
		ano.appendChild(xmldoc.createTextNode(getAno().toString() ));
		mes.appendChild(xmldoc.createTextNode(getMes().toString() ));
		numDiasCotizados.appendChild(xmldoc.createTextNode(getNumDiasCotizados().toString() ));
		baseCotizacionContingenciasComunes.appendChild(xmldoc.createTextNode(getBaseCotizacionContingenciasComunes().toString() ));
		baseCotizacionDesempleo.appendChild(xmldoc.createTextNode(getBaseCotizacionDesempleo().toString() ));
		observaciones.appendChild(xmldoc.createTextNode(getObservaciones() ));
		
		Element datosCotizacion = xmldoc.createElement(DATOS_COTIZACION);
		datosCotizacion.appendChild(ano);
		datosCotizacion.appendChild(mes);
		datosCotizacion.appendChild(numDiasCotizados);
		datosCotizacion.appendChild(baseCotizacionContingenciasComunes);
		datosCotizacion.appendChild(baseCotizacionDesempleo);
		datosCotizacion.appendChild(observaciones);
		
		return datosCotizacion;
	}
	
}
