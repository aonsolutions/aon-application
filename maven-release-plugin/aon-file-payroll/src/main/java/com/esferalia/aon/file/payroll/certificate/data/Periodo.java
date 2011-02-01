package com.esferalia.aon.file.payroll.certificate.data;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Periodo {
	
	private static final String PERIODO = "Periodo";
	private static final String TIPO_DISTRIBUCION = "TipoDistribucion";
	private static final String FECHA_INICIO_PERIODO = "FechaInicioPeriodo";
	private static final String FECHA_FIN_PERIODO = "FechaFinPeriodo";
	private static final String NUMERO_DIAS_TRAB_POR_SEMANA_O_PERIODO = "NumeroDiasTrabajadosPorSemanaOPeriodo";
	
	private String tipoDistribucion;
	private String fechaInicioPeriodo;
	private String fechaFinPeriodo;
	private String numeroDiasTrabajadosPorSemanaOPeriodo;

	public String getTipoDistribucion() {
		return tipoDistribucion;
	}
	public void setTipoDistribucion(String tipoDistribucion) {
		this.tipoDistribucion = tipoDistribucion;
	}
	public String getFechaInicioPeriodo() {
		return fechaInicioPeriodo;
	}
	public void setFechaInicioPeriodo(String fechaInicioPeriodo) {
		this.fechaInicioPeriodo = fechaInicioPeriodo;
	}
	public String getFechaFinPeriodo() {
		return fechaFinPeriodo;
	}
	public void setFechaFinPeriodo(String fechaFinPeriodo) {
		this.fechaFinPeriodo = fechaFinPeriodo;
	}
	public String getNumeroDiasTrabajadosPorSemanaOPeriodo() {
		return numeroDiasTrabajadosPorSemanaOPeriodo;
	}
	public void setNumeroDiasTrabajadosPorSemanaOPeriodo(
			String numeroDiasTrabajadosPorSemanaOPeriodo) {
		this.numeroDiasTrabajadosPorSemanaOPeriodo = numeroDiasTrabajadosPorSemanaOPeriodo;
	}
	
	public Element getElement(Document xmldoc) {
		Element periodo = xmldoc.createElement(PERIODO);

		Element tipoDistribucion = xmldoc.createElement(TIPO_DISTRIBUCION);
		tipoDistribucion.appendChild(xmldoc.createTextNode(getTipoDistribucion() ));
		periodo.appendChild(tipoDistribucion);

		Element fechaInicioPeriodo = xmldoc.createElement(FECHA_INICIO_PERIODO);
		fechaInicioPeriodo.appendChild(xmldoc.createTextNode(getFechaInicioPeriodo() ));
		periodo.appendChild(fechaInicioPeriodo);
		
		Element fechaFinPeriodo = xmldoc.createElement(FECHA_FIN_PERIODO);
		fechaFinPeriodo.appendChild(xmldoc.createTextNode(getFechaFinPeriodo() ));
		periodo.appendChild(fechaFinPeriodo);
		
		Element numeroDiasTrabajadosPorSemanaOPeriodo = xmldoc.createElement(NUMERO_DIAS_TRAB_POR_SEMANA_O_PERIODO);
		numeroDiasTrabajadosPorSemanaOPeriodo.appendChild(xmldoc.createTextNode(getNumeroDiasTrabajadosPorSemanaOPeriodo() ));
		periodo.appendChild(numeroDiasTrabajadosPorSemanaOPeriodo);
		
		return periodo;
	}
	
}
