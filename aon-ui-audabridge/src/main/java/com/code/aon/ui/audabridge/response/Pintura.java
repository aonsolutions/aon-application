package com.code.aon.ui.audabridge.response;

import java.io.Serializable;

import com.code.aon.AonVersion;

public class Pintura implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String simbolo;
	private String posicion;
	private String numeroOperacion;
	private String descripcionPieza;
	private String descripcionPintura;
	private double numeroUt;
	private double importeMaterial;
	private double descuento;
	private String cambioImporte;
	
	public String getSimbolo() {
		return simbolo;
	}
	public void setSimbolo(String simbolo) {
		this.simbolo = simbolo;
	}
	public String getPosicion() {
		return posicion;
	}
	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}
	public String getNumeroOperacion() {
		return numeroOperacion;
	}
	public void setNumeroOperacion(String numeroOperacion) {
		this.numeroOperacion = numeroOperacion;
	}
	public String getDescripcionPieza() {
		return descripcionPieza;
	}
	public void setDescripcionPieza(String descripcionPieza) {
		this.descripcionPieza = descripcionPieza;
	}
	public String getDescripcionPintura() {
		return descripcionPintura;
	}
	public void setDescripcionPintura(String descripcionPintura) {
		this.descripcionPintura = descripcionPintura;
	}
	public double getNumeroUt() {
		return numeroUt;
	}
	public void setNumeroUt(double numeroUt) {
		this.numeroUt = numeroUt;
	}
	public double getImporteMaterial() {
		return importeMaterial;
	}
	public void setImporteMaterial(double importeMaterial) {
		this.importeMaterial = importeMaterial;
	}
	public double getDescuento() {
		return descuento;
	}
	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}
	public String getCambioImporte() {
		return cambioImporte;
	}
	public void setCambioImporte(String cambioImporte) {
		this.cambioImporte = cambioImporte;
	}
	
}
