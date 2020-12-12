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
		if (AonStringUtils.isBlank(i)) return OTHER;
		if(CASH_BASIS.getDescription().equalsIgnoreCase(i) || CASH_BASIS.name().equalsIgnoreCase(i) || "METALICO".equalsIgnoreCase(i)) {
			return CASH_BASIS;
		} else if(NEGOTIABLE_DOCUMENT.getDescription().equalsIgnoreCase(i) || NEGOTIABLE_DOCUMENT.name().equalsIgnoreCase(i)) {
			return NEGOTIABLE_DOCUMENT;
		} else if(DEBIT_CARD.getDescription().equalsIgnoreCase(i) || DEBIT_CARD.name().equalsIgnoreCase(i) || "TARJETA DEBITO".equalsIgnoreCase(i)) {
			return DEBIT_CARD;
		} else if(CREDIT_CARD.getDescription().equalsIgnoreCase(i) || CREDIT_CARD.name().equalsIgnoreCase(i) || "TARJETA CREDITO".equalsIgnoreCase(i)) {
			return CREDIT_CARD;
		} else if(CHEQUE.getDescription().equalsIgnoreCase(i) || CHEQUE.name().equalsIgnoreCase(i)) {
			return CHEQUE;
		} else if(BANK_TRANSFER.getDescription().equalsIgnoreCase(i) || BANK_TRANSFER.name().equalsIgnoreCase(i)) {
			return BANK_TRANSFER;
		}
		return OTHER;
	}

}