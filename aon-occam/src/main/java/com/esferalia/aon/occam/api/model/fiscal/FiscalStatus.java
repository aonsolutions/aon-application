package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public enum FiscalStatus implements Serializable {
	 
	 PENDING("Pendiente")
	,FINISHED("Finalizado")
	,BATCHED("En Lote")
	,BLOCKED("Bloqueado")
	,SENT("Presentado")
	,MISSING("Desconocido")
	;
	
	private String name;
	
	private FiscalStatus(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
