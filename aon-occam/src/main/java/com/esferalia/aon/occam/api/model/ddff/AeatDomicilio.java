package com.esferalia.aon.occam.api.model.ddff;

import java.io.Serializable;
import java.util.Date;

public class AeatDomicilio implements Serializable {
	
	private static final long serialVersionUID = -7402912805618883395L;
	
	private String tipoVia;
	private String codVia;
	private String nombreLargo;
	private String nombreCorto;
	private String numeracion;
	private String numero;
	private String calificadorNumero;
	private String bloque;
	private String portal;
	private String escalera;
	private String planta;	
	private String puerta;
	private String datosComplementarios;	
	private String poblacion;
	private String codigoPostal;	
	private String codigoMunicipio;
	private String municipio;
	private String codigoProvincia;
	private String provincia;
	private String referenciaCatastral;
	private Date fechaModif;
	
	public String getTipoVia() {
		return tipoVia;
	}
	public AeatDomicilio setTipoVia(String tipoVia) {
		this.tipoVia = tipoVia;
		return this;
	}
	
	public String getCodVia() {
		return codVia;
	}
	public AeatDomicilio setCodVia(String codVia) {
		this.codVia = codVia;
		return this;
	}
	
	public String getNombreLargo() {
		return nombreLargo;
	}
	public AeatDomicilio setNombreLargo(String nombreLargo) {
		this.nombreLargo = nombreLargo;
		return this;
	}

	public String getNombreCorto() {
		return nombreCorto;
	}
	public AeatDomicilio setNombreCorto(String nombreCorto) {
		this.nombreCorto = nombreCorto;
		return this;
	}

	public String getNumeracion() {
		return numeracion;
	}
	public AeatDomicilio setNumeracion(String numeracion) {
		this.numeracion = numeracion;
		return this;
	}
	
	public String getNumero() {
		return numero;
	}
	public AeatDomicilio setNumero(String numero) {
		this.numero = numero;
		return this;
	}

	public String getCalificadorNumero() {
		return calificadorNumero;
	}
	public AeatDomicilio setCalificadorNumero(String calificadorNumero) {
		this.calificadorNumero = calificadorNumero;
		return this;
	}

	public String getBloque() {
		return bloque;
	}
	public AeatDomicilio setBloque(String bloque) {
		this.bloque = bloque;
		return this;
	}

	public String getPortal() {
		return portal;
	}
	public AeatDomicilio setPortal(String portal) {
		this.portal = portal;
		return this;
	}

	public String getEscalera() {
		return escalera;
	}
	public AeatDomicilio setEscalera(String escalera) {
		this.escalera = escalera;
		return this;
	}

	public String getPlanta() {
		return planta;
	}
	public AeatDomicilio setPlanta(String planta) {
		this.planta = planta;
		return this;
	}

	public String getPuerta() {
		return puerta;
	}
	public AeatDomicilio setPuerta(String puerta) {
		this.puerta = puerta;
		return this;
	}

	public String getDatosComplementarios() {
		return datosComplementarios;
	}
	public AeatDomicilio setDatosComplementarios(String datosComplementarios) {
		this.datosComplementarios = datosComplementarios;
		return this;
	}

	public String getPoblacion() {
		return poblacion;
	}
	public AeatDomicilio setPoblacion(String poblacion) {
		this.poblacion = poblacion;
		return this;
	}

	public String getCodigoPostal() {
		return codigoPostal;
	}
	public AeatDomicilio setCodigoPostal(String codigoPostal) {
		this.codigoPostal = codigoPostal;
		return this;
	}

	public String getCodigoMunicipio() {
		return codigoMunicipio;
	}
	public AeatDomicilio setCodigoMunicipio(String codigoMunicipio) {
		this.codigoMunicipio = codigoMunicipio;
		return this;
	}

	public String getMunicipio() {
		return municipio;
	}
	public AeatDomicilio setMunicipio(String municipio) {
		this.municipio = municipio;
		return this;
	}

	public String getCodigoProvincia() {
		return codigoProvincia;
	}
	public AeatDomicilio setCodigoProvincia(String codigoProvincia) {
		this.codigoProvincia = codigoProvincia;
		return this;
	}

	public String getProvincia() {
		return provincia;
	}
	public AeatDomicilio setProvincia(String provincia) {
		this.provincia = provincia;
		return this;
	}

	public String getReferenciaCatastral() {
		return referenciaCatastral;
	}
	public AeatDomicilio setReferenciaCatastral(String referenciaCatastral) {
		this.referenciaCatastral = referenciaCatastral;
		return this;
	}

	public Date getFechaModif() {
		return fechaModif;
	}
	public AeatDomicilio setFechaModif(Date fechaModif) {
		this.fechaModif = fechaModif;
		return this;
	}
}