package com.esferalia.aon.file.payroll.fdi.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ETI {

	private Integer clave;
	private Integer fecha;
	private Integer hora;
	private String fichero;
	private String identificacion;
	private List<EMP> empresas;
	private ETF etf;

	public ETI() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String f =  formatter.format(date);
		fecha = Integer.parseInt(f);
		formatter = new SimpleDateFormat("HHmm");
		String t =  formatter.format(date);
		hora = Integer.parseInt(t);
		formatter = new SimpleDateFormat("ddHHmmss");
		fichero =  formatter.format(date);
		
		etf = new ETF();
		etf.setFichero(fichero);
		etf.setFecha(fecha);
		etf.setHora(hora);
	}
	
	public Integer getClave() {
		return clave;
	}
	public void setClave(Integer clave) {
		this.clave = clave;
		etf.setClave(clave);
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

	public String getIdentificacion() {
		return identificacion;
	}
	public void setIdentificacion(String identificacion) {
		this.identificacion = identificacion;
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
