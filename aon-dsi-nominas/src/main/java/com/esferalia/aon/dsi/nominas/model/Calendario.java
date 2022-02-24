package com.esferalia.aon.dsi.nominas.model;

import java.util.Date;
import java.util.LinkedList;

import com.esferalia.aon.watson.util.AonStringUtils;

public class Calendario {
	
	String codigo;
	String nombre;
	
	private LinkedList<Date> festivos;
	
	Integer holiday; // ID en la tabla de AON (holiday), lo rellena el proceso de traspaso
	
	public String getCodigo() {
		return codigo;
	}
	public Calendario setCodigo(String codigo) {
		this.codigo = codigo;
		return this;
	}
	public String getNombre() {
		return nombre;
	}
	public Calendario setNombre(String nombre) {
		this.nombre = nombre;
		return this;
	}
	public LinkedList<Date> getFestivos() {
		if (festivos == null) {
			festivos = new LinkedList<Date>();
		}			
		return festivos;
	}
	public Calendario setFestivos(LinkedList<Date> festivos) {
		this.festivos = festivos;
		return this;
	}
	
	public Integer getHoliday() {
		return holiday;
	}
	public Calendario setHoliday(Integer holiday) {
		this.holiday = holiday;
		return this;
	}
	
	// Descripción del Calendario de Omega que se graba en AON (holiday) 
	public String getAonDescription() {
		return "Festivos " + AonStringUtils.trimToEmpty(this.nombre).toUpperCase() + " [" + this.codigo + "]";		
	}
	
	@Override
	public String toString() {
		return "Calendario [codigo=" + codigo + ", nombre=" + nombre + "]";
//		return "Calendario [codigo=" + codigo + ", nombre=" + nombre + ", holiday=" + holiday + ", festivos=" + festivos + "]";
	}
	

}
