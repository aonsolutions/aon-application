package com.esferalia.aon.occam.api.model.finance;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum PrintInvoiceTheme {

	BLACK_AND_WHITE,
	AON_BLUE,
	PERSONALIZED;
	
	private PrintInvoiceTheme() {
	
	}
	
	public Byte value() {
		return (byte) ordinal();
	}
	
	public static PrintInvoiceTheme safeValueOf( Byte i ) {
		if (i == null) return null;
		if (i < 0 || i >= PrintInvoiceTheme.values().length) return BLACK_AND_WHITE;
		return PrintInvoiceTheme.values()[i];
	}
	
	public static PrintInvoiceTheme safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= PrintInvoiceTheme.values().length) return BLACK_AND_WHITE;
		return PrintInvoiceTheme.values()[i];
	}
	
	public static PrintInvoiceTheme safeValueOf( String str) {
		if(AonStringUtils.isBlank(str)) return BLACK_AND_WHITE;
		for (PrintInvoiceTheme rs : values()) {
			if(rs.name().equalsIgnoreCase(str))
				return rs;
		}
		return BLACK_AND_WHITE;
	}
	
}
