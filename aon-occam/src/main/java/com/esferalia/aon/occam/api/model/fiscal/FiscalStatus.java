package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public enum FiscalStatus implements Serializable {
	 
	 MISSING("Desconocido")
	,PENDING("Pendiente")
	,FINISHED("Finalizado")
	;
	
	private String name;
	
	private FiscalStatus(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
