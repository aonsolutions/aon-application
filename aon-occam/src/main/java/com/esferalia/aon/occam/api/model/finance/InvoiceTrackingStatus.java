package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceTrackingStatus implements Serializable {
	
	 DELETED
	;
	
	private InvoiceTrackingStatus() {

	}
	
	public Byte value() {
		return (byte) ordinal();
	}
	
	public static InvoiceTrackingStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvoiceTrackingStatus.values().length) return null;
		return InvoiceTrackingStatus.values()[i];
	}
	
	public static InvoiceTrackingStatus safeValueOf( String str) {
		if(AonStringUtils.isBlank(str)) return DELETED;
		for (InvoiceTrackingStatus rs : values()) {
			if(rs.name().equalsIgnoreCase(str))
				return rs;
		}
		return DELETED;
	}
	
}
