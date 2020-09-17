package com.esferalia.aon.occam.api.model.aonsolutions;

import java.io.Serializable;
import java.util.Arrays;
import java.util.stream.Stream;

public enum AonApp implements Serializable{
	INVOICE,
	DOCUMENTAL,
	HELPDESK,
	ACCOUNTING,
	FISCAL,
	PAYROLL,
	OCR,
	AIO,
	TOOLS,
	CONTRATA,
	PORTAL,
	MESSENGER,
	CONVENIOS;
	
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
		} else if(i.equalsIgnoreCase(HELPDESK.name())) {
			return HELPDESK;
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
		} else if(i.equalsIgnoreCase(CONTRATA.name())) {
			return CONTRATA;
		} else if(i.equalsIgnoreCase(PORTAL.name())) {
			return PORTAL;
		} else if(i.equalsIgnoreCase(MESSENGER.name())) {
			return MESSENGER;
		} else if(i.equalsIgnoreCase(CONVENIOS.name())) {
			return CONVENIOS;
		} 
		return null;
	}
	
	public static Stream<AonApp> aonValues() {
		AonApp[] array = {INVOICE, DOCUMENTAL, HELPDESK, ACCOUNTING, FISCAL, PAYROLL, OCR, AIO, TOOLS};
		return Arrays.stream(array);
	}
}
