package com.esferalia.aon.occam.api.model.ddff;

import java.io.Serializable;

public class AeatCotizacionAutonomo implements Serializable {
	
	private static final long serialVersionUID = -7809509188923844765L;
	
	private String numeroAfiliacion;
	private AeatRegCotizacion regCotizacion;
	private Double importe;
	
	public String getNumeroAfiliacion() {
		return numeroAfiliacion;
	}
	public AeatCotizacionAutonomo setNumeroAfiliacion(String numeroAfiliacion) {
		this.numeroAfiliacion = numeroAfiliacion;
		return this;
	}
	
	public AeatRegCotizacion getRegCotizacion() {
		return regCotizacion;
	}
	public AeatCotizacionAutonomo setRegCotizacion(AeatRegCotizacion regCotizacion) {
		this.regCotizacion = regCotizacion;
		return this;
	}

	public Double getImporte() {
		return importe;
	}
	public AeatCotizacionAutonomo setImporte(Double importe) {
		this.importe = importe;
		return this;
	}
	
}