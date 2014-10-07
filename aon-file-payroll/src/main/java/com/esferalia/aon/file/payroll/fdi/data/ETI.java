package com.esferalia.aon.file.payroll.fdi.data;

import java.util.LinkedList;
import java.util.List;

public class ETI {

	private String identificador;
	private String clave;
	private Integer fecha;
	private Integer hora;
	private String fichero;
	private List<EMP> empresas;
	private ETF etf;
	
	public String getIdentificador() {
		return identificador;
	}
	public void setIdentificador(String identificador) {
		this.identificador = identificador;
	}
	
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public Integer getFecha() {
		return fecha;
	}
	public void setFecha(Integer fecha) {
		this.fecha = fecha;
	}

	public Integer getHora() {
		return hora;
	}
	public void setHora(Integer hora) {
		this.hora = hora;
	}

	public String getFichero() {
		return fichero;
	}
	public void setFichero(String fichero) {
		this.fichero = fichero;
	}
	
	public List<EMP> getEmpresas() {
		if (empresas == null) {
			empresas = new LinkedList<EMP>();
		}
		return empresas;
	}

	public void setEmpresas(List<EMP> empresas) {
		this.empresas = empresas;
	}

	public ETF getEtf() {
		return etf;
	}
	public void setEtf(ETF etf) {
		this.etf = etf;
	}
	
}
