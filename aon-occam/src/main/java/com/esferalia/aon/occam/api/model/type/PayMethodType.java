package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum PayMethodType implements Serializable {
	  
	CASH_BASIS("Met\u00E1lico", "CASH"),
	NEGOTIABLE_DOCUMENT("Negociable", "BANK"),
	DEBIT_CARD("Tarjeta D\u00E9bito", "CARD"),
	CREDIT_CARD("Tarjeta Cr\u00E9dito", "CARD"),
	CHEQUE("Cheque", "DRAFT"),
	BANK_TRANSFER("Transferencia", "TRANSFER"),
	OTHER("Otros", "OTHER");
	
	private String description;
	private String tediName;
	
	private PayMethodType(String description, String tediName) {
		this.description = description;
		this.tediName = tediName;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getTediName() {
		return tediName;
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
		if(CASH_BASIS.getDescription().equalsIgnoreCase(i) || CASH_BASIS.name().equalsIgnoreCase(i) || "METALICO".equalsIgnoreCase(i) || CASH_BASIS.getTediName().equalsIgnoreCase(i)) {
			return CASH_BASIS;
		} else if(NEGOTIABLE_DOCUMENT.getDescription().equalsIgnoreCase(i) || NEGOTIABLE_DOCUMENT.name().equalsIgnoreCase(i) || NEGOTIABLE_DOCUMENT.getTediName().equalsIgnoreCase(i)) {
			return NEGOTIABLE_DOCUMENT;
		} else if(DEBIT_CARD.getDescription().equalsIgnoreCase(i) || DEBIT_CARD.name().equalsIgnoreCase(i) || "TARJETA DEBITO".equalsIgnoreCase(i) || DEBIT_CARD.getTediName().equalsIgnoreCase(i)) {
			return DEBIT_CARD;
		} else if(CREDIT_CARD.getDescription().equalsIgnoreCase(i) || CREDIT_CARD.name().equalsIgnoreCase(i) || "TARJETA CREDITO".equalsIgnoreCase(i) || CREDIT_CARD.getTediName().equalsIgnoreCase(i)) {
			return CREDIT_CARD;
		} else if(CHEQUE.getDescription().equalsIgnoreCase(i) || CHEQUE.name().equalsIgnoreCase(i) || CHEQUE.getTediName().equalsIgnoreCase(i)) {
			return CHEQUE;
		} else if(BANK_TRANSFER.getDescription().equalsIgnoreCase(i) || BANK_TRANSFER.name().equalsIgnoreCase(i) || BANK_TRANSFER.getTediName().equalsIgnoreCase(i)) {
			return BANK_TRANSFER;
		}
		return OTHER;
	}

}