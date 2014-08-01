package com.code.aon.ui.audabridge.response;

import java.io.Serializable;

import com.code.aon.AonVersion;

public class Pieza implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String tipoLinea;
	private String simbolo;
	private String posicionDb;
	private String descripcion;
	private String numeroPieza;
	private double precio;
	private String cambioPrecio;
	private double descuento;
	
	public String getTipoLinea() {
		return tipoLinea;
	}
	public void setTipoLinea(String tipoLinea) {
		this.tipoLinea = tipoLinea;
	}
	public String getSimbolo() {
		return simbolo;
	}
	public void setSimbolo(String simbolo) {
		this.simbolo = simbolo;
	}
	public String getPosicionDb() {
		return posicionDb;
	}
	public void setPosicionDb(String posicionDb) {
		this.posicionDb = posicionDb;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getNumeroPieza() {
		return numeroPieza;
	}
	public void setNumeroPieza(String numeroPieza) {
		this.numeroPieza = numeroPieza;
	}
	public double getPrecio() {
		return precio;
	}
	public void setPrecio(double precio) {
		this.precio = precio;
	}
	public String getCambioPrecio() {
		return cambioPrecio;
	}
	public void setCambioPrecio(String cambioPrecio) {
		this.cambioPrecio = cambioPrecio;
	}
	public double getDescuento() {
		return descuento;
	}
	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}
	
}
