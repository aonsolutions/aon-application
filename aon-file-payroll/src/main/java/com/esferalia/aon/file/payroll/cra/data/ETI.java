package com.esferalia.aon.file.payroll.cra.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class ETI {

	
	private Integer clave;
	private Integer fecha;
	private Integer hora;
	private String nombreFichero;
	private String prueba;
	private List<DDE> ddeList;
	
	public ETI() {
		Date date = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
		String f =  formatter.format(date);
		fecha = Integer.parseInt(f);
		formatter = new SimpleDateFormat("HHmm");
		String t =  formatter.format(date);
		hora = Integer.parseInt(t);
		formatter = new SimpleDateFormat("ddHHmmss");
		nombreFichero =  formatter.format(date);
		
	}
	
	public Integer getClave() {
		return clave;
	}
	public void setClave(Integer clave) {
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
	
	public String getPrueba() {
		return prueba;
	}

	public void setPrueba(String prueba) {
		this.prueba = prueba;
	}

	public String getNombreFichero() {
		return nombreFichero;
	}

	public void setNombreFichero(String nombreFichero) {
		this.nombreFichero = nombreFichero;
	}

	public List<DDE> getDdeList() {
		if(ddeList==null){
			ddeList = new LinkedList<DDE>();
		}
		return ddeList;
	}

	public void setDdeList(List<DDE> ddeList) {
		this.ddeList = ddeList;
	}

}
