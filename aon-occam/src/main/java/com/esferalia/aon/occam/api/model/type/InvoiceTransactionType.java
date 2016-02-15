package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum InvoiceTransactionType implements Serializable {

	NATIONAL ("Nacional"),
	INTRACOMMUNITY("Intracomunitaria"),
	EXTRACOMMUNITY("Extracomunitaria"),
	CAN_CEU_MEL("Canarias, Ceuta y Melilla"),
	OTHER_ISP("I.S.P.");
	
	private String description;
	
	private InvoiceTransactionType(String description) {
		this.description = description;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getDescription() {
		return description;
	}

	public static InvoiceTransactionType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static InvoiceTransactionType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i > InvoiceTransactionType.values().length) return null;
		return InvoiceTransactionType.values()[i];
	}

}