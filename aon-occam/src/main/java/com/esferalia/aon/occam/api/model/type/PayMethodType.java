package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum PayMethodType implements Serializable {

	CASH_BASIS("Met\u00E1lico"),
	NEGOTIABLE_DOCUMENT("Negociable"),
	DEBIT_CARD("Tarjeta D\u00E9bito"),
	CREDIT_CARD("Tarjeta Cr\u00E9dito"),
	CHEQUE("Cheque"),
	BANK_TRANSFER("Transferencia"),
	OTHER("Otros");
	
	private String description;
	
	private PayMethodType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}

	public static PayMethodType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static PayMethodType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= PayMethodType.values().length) return null;
		return PayMethodType.values()[i];
	}
	
	public static PayMethodType safeValueOf( String i ) {
		if (AonStringUtils.isBlank(i)) return null;
		PayMethodType p = null; 
		for (PayMethodType pmt : values()) {
			if(i.equalsIgnoreCase(pmt.getDescription()) || i.equalsIgnoreCase(pmt.name())) {
				p = pmt;
			}
		}
		return p;
	}

}