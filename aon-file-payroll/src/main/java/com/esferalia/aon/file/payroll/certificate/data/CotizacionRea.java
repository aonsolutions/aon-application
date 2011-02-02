package com.esferalia.aon.file.payroll.certificate.data;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class CotizacionRea {
	
	private static final String DATOS_COTIZACION_REA = "Datos_Cotizacion_REA";
	private static final String ANO = "Ano";
	private static final String MES = "Mes";
	private static final String GRUPO_COTIZACION = "GrupoCotizacion";
	private static final String NUM_DIAS_COTIZADOS = "NumDiasCotizados";
	private static final String NUM_JORNADAS_COTIZADAS = "NumJornadasCotizadas";
	private static final String OBSERVACIONES = "Observaciones";
	
	private String ano;
	private String mes;
	private String grupoCotizacion;
	private String numDiasCotizados;
	private String numJornadasCotizadas;
	private String observaciones;

	public String getAno() {
		return ano;
	}
	public void setAno(String ano) {
		this.ano = ano;
	}
	public String getMes() {
		return mes;
	}
	public void setMes(String mes) {
		this.mes = mes;
	}
	public String getGrupoCotizacion() {
		return grupoCotizacion;
	}
	public void setGrupoCotizacion(String grupoCotizacion) {
		this.grupoCotizacion = grupoCotizacion;
	}
	public String getNumDiasCotizados() {
		return numDiasCotizados;
	}
	public void setNumDiasCotizados(String numDiasCotizados) {
		this.numDiasCotizados = numDiasCotizados;
	}
	public String getNumJornadasCotizadas() {
		return numJornadasCotizadas;
	}
	public void setNumJornadasCotizadas(String numJornadasCotizadas) {
		this.numJornadasCotizadas = numJornadasCotizadas;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	
	public Element getElement(Document xmldoc) {
		Element ano = xmldoc.createElement(ANO);
		Element mes = xmldoc.createElement(MES);
		Element grupoCotizacion = xmldoc.createElement(GRUPO_COTIZACION);
		Element numDiasCotizados = xmldoc.createElement(NUM_DIAS_COTIZADOS);
		Element numJornadasCotizadas = xmldoc.createElement(NUM_JORNADAS_COTIZADAS);
		Element observaciones = xmldoc.createElement(OBSERVACIONES);
		
		ano.appendChild(xmldoc.createTextNode(getAno().toString()));
		mes.appendChild(xmldoc.createTextNode(getMes().toString()));
		grupoCotizacion.appendChild(xmldoc.createTextNode(getGrupoCotizacion().toString()));
		numDiasCotizados.appendChild(xmldoc.createTextNode(getNumDiasCotizados().toString()));
		numJornadasCotizadas.appendChild(xmldoc.createTextNode(getNumJornadasCotizadas().toString()));
		observaciones.appendChild(xmldoc.createTextNode(getObservaciones()));

		Element datosCotizacionRea = xmldoc.createElement(DATOS_COTIZACION_REA);
		datosCotizacionRea.appendChild(ano);
		datosCotizacionRea.appendChild(mes);
		datosCotizacionRea.appendChild(grupoCotizacion);
		datosCotizacionRea.appendChild(numDiasCotizados);
		datosCotizacionRea.appendChild(numJornadasCotizadas);
		datosCotizacionRea.appendChild(observaciones);
		
		return null;
	}
	
}
