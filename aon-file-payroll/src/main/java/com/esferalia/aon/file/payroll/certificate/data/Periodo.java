package com.esferalia.aon.file.payroll.certificate.data;

import java.util.Date;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Periodo {
	
	private static final String PERIODO = "Periodo";
	private static final String TIPO_DISTRIBUCION = "TipoDistribucion";
	private static final String FECHA_INICIO_PERIODO = "FechaInicioPeriodo";
	private static final String FECHA_FIN_PERIODO = "FechaFinPeriodo";
	private static final String NUMERO_DIAS_TRAB_POR_SEMANA_O_PERIODO = "NumeroDiasTrabajadosPorSemanaOPeriodo";
	
	private String tipoDistribucion;
	private Date fechaInicioPeriodo;
	private Date fechaFinPeriodo;
	private Integer numeroDiasTrabajadosPorSemanaOPeriodo;

	public String getTipoDistribucion() {
		return tipoDistribucion;
	}
	public void setTipoDistribucion(String tipoDistribucion) {
		this.tipoDistribucion = tipoDistribucion;
	}
	public Date getFechaInicioPeriodo() {
		return fechaInicioPeriodo;
	}
	public void setFechaInicioPeriodo(Date fechaInicioPeriodo) {
		this.fechaInicioPeriodo = fechaInicioPeriodo;
	}
	public Date getFechaFinPeriodo() {
		return fechaFinPeriodo;
	}
	public void setFechaFinPeriodo(Date fechaFinPeriodo) {
		this.fechaFinPeriodo = fechaFinPeriodo;
	}
	public Integer getNumeroDiasTrabajadosPorSemanaOPeriodo() {
		return numeroDiasTrabajadosPorSemanaOPeriodo;
	}
	public void setNumeroDiasTrabajadosPorSemanaOPeriodo(
			Integer numeroDiasTrabajadosPorSemanaOPeriodo) {
		this.numeroDiasTrabajadosPorSemanaOPeriodo = numeroDiasTrabajadosPorSemanaOPeriodo;
	}
	
	public Element getElement(Document xmldoc) {
		Element periodo = xmldoc.createElement(PERIODO);

		Element tipoDistribucion = xmldoc.createElement(TIPO_DISTRIBUCION);
		tipoDistribucion.appendChild(xmldoc.createTextNode(getTipoDistribucion() ));
		periodo.appendChild(tipoDistribucion);

		Element fechaInicioPeriodo = xmldoc.createElement(FECHA_INICIO_PERIODO);
		fechaInicioPeriodo.appendChild(xmldoc.createTextNode(getFechaInicioPeriodo().toString() ));
		periodo.appendChild(fechaInicioPeriodo);
		
		Element fechaFinPeriodo = xmldoc.createElement(FECHA_FIN_PERIODO);
		fechaFinPeriodo.appendChild(xmldoc.createTextNode(getFechaFinPeriodo().toString() ));
		periodo.appendChild(fechaFinPeriodo);
		
		Element numeroDiasTrabajadosPorSemanaOPeriodo = xmldoc.createElement(NUMERO_DIAS_TRAB_POR_SEMANA_O_PERIODO);
		numeroDiasTrabajadosPorSemanaOPeriodo.appendChild(xmldoc.createTextNode(getNumeroDiasTrabajadosPorSemanaOPeriodo().toString() ));
		periodo.appendChild(numeroDiasTrabajadosPorSemanaOPeriodo);
		
		return periodo;
	}
	
}
