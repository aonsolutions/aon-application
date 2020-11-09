package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;

public enum AonApp implements Serializable{
	INVOICE,
	DOCUMENTAL,
	MESSENGER,
	ACCOUNTING,
	FISCAL,
	PAYROLL,
	OCR,
	AIO,
	TOOLS,
	COMUNICA,
	PORTAL,
	CONVENIOS,
	BANK;
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static AonApp safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static AonApp safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= AonApp.values().length) return null;
		return AonApp.values()[i];
	}
	
	public static AonApp safeValueOf( String i ) {
		if (i == null || "".equals(i)) return null;
		if(i.equalsIgnoreCase(INVOICE.name())) {
			return INVOICE;
		} else if(i.equalsIgnoreCase(DOCUMENTAL.name())) {
			return DOCUMENTAL;
		} else if(i.equalsIgnoreCase(BANK.name())) {
			return BANK;
		} else if(i.equalsIgnoreCase(ACCOUNTING.name())) {
			return ACCOUNTING;
		} else if(i.equalsIgnoreCase(FISCAL.name())) {
			return FISCAL;
		} else if(i.equalsIgnoreCase(PAYROLL.name())) {
			return PAYROLL;
		} else if(i.equalsIgnoreCase(OCR.name())) {
			return OCR;
		} else if(i.equalsIgnoreCase(AIO.name())) {
			return AIO;
		} else if(i.equalsIgnoreCase(TOOLS.name())) {
			return TOOLS;
		} else if(i.equalsIgnoreCase(COMUNICA.name())) {
			return COMUNICA;
		} else if(i.equalsIgnoreCase(PORTAL.name())) {
			return PORTAL;
		} else if(i.equalsIgnoreCase(MESSENGER.name())) {
			return MESSENGER;
		} else if(i.equalsIgnoreCase(CONVENIOS.name())) {
			return CONVENIOS;
		} 
		return null;
	}
	
}
