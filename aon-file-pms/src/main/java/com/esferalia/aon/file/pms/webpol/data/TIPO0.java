package com.esferalia.aon.file.pms.webpol.data;

import java.util.List;

/**
 * 
 * TIPO0 - Registro del tipo 0
 * 
 * solo si se trata de una agrupacion hotelera
 * 
 */
public class TIPO0 {
	
	private String codigoAgrupacionHotelera;
	private String nombreAgrupacion;
	private String fechaConfeccionFichero;
	private String horaConfeccionFichero;
	private String numeroRegistrosTipo1;
	
	private List<TIPO1> tipo1List;
	
	
	public String getCodigoAgrupacionHotelera() {
		return codigoAgrupacionHotelera;
	}
	public void setCodigoAgrupacionHotelera(String codigoAgrupacionHotelera) {
		this.codigoAgrupacionHotelera = codigoAgrupacionHotelera;
	}
	public String getNombreAgrupacion() {
		return nombreAgrupacion;
	}
	public void setNombreAgrupacion(String nombreAgrupacion) {
		this.nombreAgrupacion = nombreAgrupacion;
	}
	public String getFechaConfeccionFichero() {
		return fechaConfeccionFichero;
	}
	public void setFechaConfeccionFichero(String fechaConfeccionFichero) {
		this.fechaConfeccionFichero = fechaConfeccionFichero;
	}
	public String getHoraConfeccionFichero() {
		return horaConfeccionFichero;
	}
	public void setHoraConfeccionFichero(String horaConfeccionFichero) {
		this.horaConfeccionFichero = horaConfeccionFichero;
	}
	public String getNumeroRegistrosTipo1() {
		return numeroRegistrosTipo1;
	}
	public void setNumeroRegistrosTipo1(String numeroRegistrosTipo1) {
		this.numeroRegistrosTipo1 = numeroRegistrosTipo1;
	}
	public List<TIPO1> getTipo1List() {
		return tipo1List;
	}
	public void setTipo1List(List<TIPO1> tipo1List) {
		this.tipo1List = tipo1List;
	}
	
}
