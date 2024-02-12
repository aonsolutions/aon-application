package com.esferalia.aon.occam.api.model.ddff;

import java.io.Serializable;

public class AeatDatosGenerales implements Serializable {
	
	private static final long serialVersionUID = -4495850711914804954L;
	
	private AeatEstadoCivil estadoCivil;
	private boolean conyugeNoResidente;
	private boolean conyugeNoResidenteUE;
	
	public AeatEstadoCivil getEstadoCivil() {
		return estadoCivil;
	}
	public AeatDatosGenerales setEstadoCivil(AeatEstadoCivil estadoCivil) {
		this.estadoCivil = estadoCivil;
		return this;
	}
	
	public boolean isConyugeNoResidente() {
		return conyugeNoResidente;
	}
	public AeatDatosGenerales setConyugeNoResidente(boolean conyugeNoResidente) {
		this.conyugeNoResidente = conyugeNoResidente;
		return this;
	}
	
	public boolean isConyugeNoResidenteUE() {
		return conyugeNoResidenteUE;
	}
	public AeatDatosGenerales setConyugeNoResidenteUE(boolean conyugeNoResidenteUE) {
		this.conyugeNoResidenteUE = conyugeNoResidenteUE;
		return this;
	}
	
}