package com.esferalia.aon.file.pms.webpol.data;

import java.util.List;

/**
 * 
 * TIPO1 - Registro del tipo 1
 * 
 * datos del establecimiento hotelero y de control
 * 
 */
public class TIPO1 {
	
	private String codigoEstablecimientoHotelero; 
	private String nombreEstablecimiento;
	private String fechaConfeccionFichero;
	private String horaConfeccionFichero;
	private String numeroRegistrosTipo2;
	
	private List<TIPO2> tipo2List;
	
	
	public String getCodigoEstablecimientoHotelero() {
		return codigoEstablecimientoHotelero;
	}
	public void setCodigoEstablecimientoHotelero(
			String codigoEstablecimientoHotelero) {
		this.codigoEstablecimientoHotelero = codigoEstablecimientoHotelero;
	}
	public String getNombreEstablecimiento() {
		return nombreEstablecimiento;
	}
	public void setNombreEstablecimiento(String nombreEstablecimiento) {
		this.nombreEstablecimiento = nombreEstablecimiento;
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
	public String getNumeroRegistrosTipo2() {
		return numeroRegistrosTipo2;
	}
	public void setNumeroRegistrosTipo2(String numeroRegistrosTipo2) {
		this.numeroRegistrosTipo2 = numeroRegistrosTipo2;
	}
	public List<TIPO2> getTipo2List() {
		return tipo2List;
	}
	public void setTipo2List(List<TIPO2> tipo2List) {
		this.tipo2List = tipo2List;
	}
	
}
