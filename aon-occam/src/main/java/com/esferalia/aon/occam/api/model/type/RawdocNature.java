package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum RawdocNature implements Serializable {
	
	 INVOICE("Factura")
	 /*
	  * NOMINA, PRESUPUESTO, PEDIDO, etc ....
	  */
	;

	private String description;
	
	private RawdocNature(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static RawdocNature safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static RawdocNature safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= RawdocNature.values().length) return null;
		return RawdocNature.values()[i];
	}
	
}
