package com.esferalia.aon.file.payroll.certificate.data;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class VacacionesRea {
	
	private static final String DATOS_VACACIONES_COTIZADAS_REA = "Datos_VacacionesCotizadasREA";
	private static final String GRUPO_COTIZACION = "GrupoCotizacion";
	private static final String NUM_DIAS_COTIZADOS = "NumDiasCotizados";
	private static final String NUM_JORNADAS_COTIZADAS = "NumJornadasCotizadas";
	
	private Integer grupoCotizacion;
	private Integer numDiasCotizados;
	private Integer numJornadasCotizadas;
	
	public Integer getGrupoCotizacion() {
		return grupoCotizacion;
	}

	public void setGrupoCotizacion(Integer grupoCotizacion) {
		this.grupoCotizacion = grupoCotizacion;
	}

	public Integer getNumDiasCotizados() {
		return numDiasCotizados;
	}

	public void setNumDiasCotizados(Integer numDiasCotizados) {
		this.numDiasCotizados = numDiasCotizados;
	}

	public Integer getNumJornadasCotizadas() {
		return numJornadasCotizadas;
	}

	public void setNumJornadasCotizadas(Integer numJornadasCotizadas) {
		this.numJornadasCotizadas = numJornadasCotizadas;
	}

	public Node getElement(Document xmldoc) {
		Element grupoCotizacion = xmldoc.createElement(GRUPO_COTIZACION);
		Element numDiasCotizados = xmldoc.createElement(NUM_DIAS_COTIZADOS);
		Element numJornadasCotizadas = xmldoc.createElement(NUM_JORNADAS_COTIZADAS);
		
		grupoCotizacion.appendChild(xmldoc.createTextNode(getGrupoCotizacion().toString()));
		numDiasCotizados.appendChild(xmldoc.createTextNode(getNumDiasCotizados().toString()));
		numJornadasCotizadas.appendChild(xmldoc.createTextNode(getNumJornadasCotizadas().toString()));
		
		Element datosVacacionesCotizadasRea = xmldoc.createElement(DATOS_VACACIONES_COTIZADAS_REA);
		datosVacacionesCotizadasRea.appendChild(grupoCotizacion);
		datosVacacionesCotizadasRea.appendChild(numDiasCotizados);
		datosVacacionesCotizadasRea.appendChild(numJornadasCotizadas);
		
		return datosVacacionesCotizadasRea;
	}
	
}
