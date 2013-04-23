package com.esferalia.aon.file.payroll.afi.data;

/**
 * domicilio del trabajador
 */
public class DOM {
	
	private String tipoDomicilio;
	private String tipoVia;
	private String nombreVia;
	private String numero;
	private String bis;
	private String bloque;
	private String escalera;
	private String piso;
	private String puerta;
	private String mensajeSms;
	private String borrarSms;
	
	private LDD ldd;
	
	public String getTipoDomicilio() {
		return tipoDomicilio;
	}
	public void setTipoDomicilio(String tipoDomicilio) {
		this.tipoDomicilio = tipoDomicilio;
	}
	public String getTipoVia() {
		return tipoVia;
	}
	public void setTipoVia(String tipoVia) {
		this.tipoVia = tipoVia;
	}
	public String getNombreVia() {
		return nombreVia;
	}
	public void setNombreVia(String nombreVia) {
		this.nombreVia = nombreVia;
	}
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getBis() {
		return bis;
	}
	public void setBis(String bis) {
		this.bis = bis;
	}
	public String getBloque() {
		return bloque;
	}
	public void setBloque(String bloque) {
		this.bloque = bloque;
	}
	public String getEscalera() {
		return escalera;
	}
	public void setEscalera(String escalera) {
		this.escalera = escalera;
	}
	public String getPiso() {
		return piso;
	}
	public void setPiso(String piso) {
		this.piso = piso;
	}
	public String getPuerta() {
		return puerta;
	}
	public void setPuerta(String puerta) {
		this.puerta = puerta;
	}
	public String getMensajeSms() {
		return mensajeSms;
	}
	public void setMensajeSms(String mensajeSms) {
		this.mensajeSms = mensajeSms;
	}
	public String getBorrarSms() {
		return borrarSms;
	}
	public void setBorrarSms(String borrarSms) {
		this.borrarSms = borrarSms;
	}
	public LDD getLdd() {
		return ldd;
	}
	public void setLdd(LDD ldd) {
		this.ldd = ldd;
	}
	
}
