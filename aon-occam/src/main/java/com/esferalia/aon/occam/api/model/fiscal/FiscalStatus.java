package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public enum FiscalStatus implements Serializable {
	 
	 PENDING("Pendiente")
	,FINISHED("Finalizado")
	,BATCHED("En Lote")
	,BLOCKED("Bloqueado")
	,SENT("Presentado")
	,MISSING("Desconocido")
	,CUSTOMER_CHECK("Envio a cliente")
	;
	
	private String name;
	
	private FiscalStatus(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public static FiscalStatus safeValueOf( String i ) {
		if (i == null) return null;
		return FiscalStatus.valueOf( i );
	}
	public static FiscalStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		if (i < 0 || i >= FiscalStatus.values().length) return null;
		return FiscalStatus.values()[i];
	}
	
}
