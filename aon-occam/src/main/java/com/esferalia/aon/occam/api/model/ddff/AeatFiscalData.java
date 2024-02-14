package com.esferalia.aon.occam.api.model.ddff;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class AeatFiscalData implements Serializable {

	private static final long serialVersionUID = -2979751110952930446L;
	
	private String error;
	private Date date;
	private List<AeatDatosGenerales> datosGenerales;
	private List<AeatTitular> titulares;
	private List<AeatDomicilio> domicilios;
	private List<AeatCotizacionAutonomo> cotizacionesAutonomo;

	public String getError() {
		return error;
	}
	public AeatFiscalData setError(String error) {
		this.error = error;
		return this;
	}
	public Date getDate() {
		return date;
	}
	public AeatFiscalData setDate(Date date) {
		this.date = date;
		return this;
	}
	
	public List<AeatDatosGenerales> getDatosGenerales() {
		return datosGenerales;
	}
	public AeatFiscalData setDatosGenerales(List<AeatDatosGenerales> datosGenerales) {
		this.datosGenerales = datosGenerales;
		return this;
	}
	public AeatFiscalData addDatosGenerales(AeatDatosGenerales datosGenerales) {
		if (this.datosGenerales == null) {
			this.datosGenerales = new LinkedList<>();
		}
		this.datosGenerales.add(datosGenerales);
		return this;
	}
	
	public List<AeatTitular> getTitulares() {
		return titulares;
	}
	public AeatFiscalData setTitulares(List<AeatTitular> titulares) {
		this.titulares = titulares;
		return this;
	}
	public AeatFiscalData addTitular(AeatTitular titular) {
		if (this.titulares == null) {
			this.titulares = new LinkedList<>();
		}
		this.titulares.add(titular);
		return this;
	}
	
	public List<AeatDomicilio> getDomicilios() {
		return domicilios;
	}
	public AeatFiscalData setDomicilios(List<AeatDomicilio> domicilios) {
		this.domicilios = domicilios;
		return this;
	}
	public AeatFiscalData addDomicilio(AeatDomicilio domicilio) {
		if (this.domicilios == null) {
			this.domicilios = new LinkedList<>();
		}
		this.domicilios.add(domicilio);
		return this;
	}
	
	public List<AeatCotizacionAutonomo> getCotizacionesAutonomo() {
		return cotizacionesAutonomo;
	}
	public AeatFiscalData setCotizacionesAutonomo(List<AeatCotizacionAutonomo> cotizacionesAutonomo) {
		this.cotizacionesAutonomo = cotizacionesAutonomo;
		return this;
	}
	public AeatFiscalData addCotizacionAutonomo(AeatCotizacionAutonomo cotizacionAutonomo) {
		if (this.cotizacionesAutonomo == null) {
			this.cotizacionesAutonomo = new LinkedList<>();
		}
		this.cotizacionesAutonomo.add(cotizacionAutonomo);
		return this;
	}

	
}
