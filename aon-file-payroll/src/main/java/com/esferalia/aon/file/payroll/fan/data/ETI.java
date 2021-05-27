package com.esferalia.aon.file.payroll.fan.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ETI {

	private String clave;
	private String proveedorNomina;
	private Integer fecha;
	private Integer hora;
	private String fichero;
	private Integer identificacion;
	private String prueba;
	private List<EMP> empresas;
	private EXC exc;
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
	
	public String getClave() {
		return clave;
	}
	public void setClave(String clave) {
		this.clave = clave;
	}
	public String getProveedorNomina() {
		return proveedorNomina;
	}
	public void setProveedorNomina(String proveedorNomina) {
		this.proveedorNomina = proveedorNomina;
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

	public Integer getIdentificacion() {
		return identificacion;
	}
	public void setIdentificacion(Integer identificacion) {
		this.identificacion = identificacion;
	}
	
	public String getPrueba() {
		return prueba;
	}

	public void setPrueba(String prueba) {
		this.prueba = prueba;
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
	
	public EXC getExc() {
		return exc;
	}

	public void setExc(EXC exc) {
		this.exc = exc;
	}

	public ETF getEtf() {
		return etf;
	}
	public void setEtf(ETF etf) {
		this.etf = etf;
	}
	
}
