package com.esferalia.aon.file.payroll.certificate.data;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
	public Element getElement(Document xmldoc) {
		Element datosCotizacion = xmldoc.createElement(DATOS_COTIZACION);

		Element ano = xmldoc.createElement(ANO);
		ano.appendChild(xmldoc.createTextNode(getAno().toString() ));
		datosCotizacion.appendChild(ano);

		Element mes = xmldoc.createElement(MES);
		mes.appendChild(xmldoc.createTextNode(getMes().toString() ));
		datosCotizacion.appendChild(mes);
		
		Element numDiasCotizados = xmldoc.createElement(NUM_DIAS_COTIZADOS);
		numDiasCotizados.appendChild(xmldoc.createTextNode(getNumDiasCotizados().toString() ));
		datosCotizacion.appendChild(numDiasCotizados);
		
		Element baseCotizacionContingenciasComunes = null;
		if(getBaseCotizacionContingenciasComunes()!=null){
			baseCotizacionContingenciasComunes = xmldoc.createElement(BASE_COTIZACION_CONTINGENCIAS_COMUNES);
			baseCotizacionContingenciasComunes.appendChild(xmldoc.createTextNode(getBaseCotizacionContingenciasComunes().toString() ));
			datosCotizacion.appendChild(baseCotizacionContingenciasComunes);
		}
		
		Element baseCotizacionDesempleo = xmldoc.createElement(BASE_COTIZACION_DESEMPLEO);
		baseCotizacionDesempleo.appendChild(xmldoc.createTextNode(getBaseCotizacionDesempleo().toString() ));
		datosCotizacion.appendChild(baseCotizacionDesempleo);
		
		Element observaciones = null;
		if(!StringUtils.isBlank(getObservaciones())){
			observaciones = xmldoc.createElement(OBSERVACIONES);
			observaciones.appendChild(xmldoc.createTextNode(getObservaciones() ));
			datosCotizacion.appendChild(observaciones);
		}
		
		return datosCotizacion;
	}
	
}
