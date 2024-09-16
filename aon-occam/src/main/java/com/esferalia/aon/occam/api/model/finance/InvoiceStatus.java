package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceStatus implements Serializable {
	 PENDING("Pendiente")
	,SCORED("Contabilizada")
	;
	
	private String name;
	
	private InvoiceStatus(String name) {
		this.name = name;
	}
	
	public Byte value() {
		return (byte) ordinal();
	}
	
	public String getName() {
		return name;
	}
	
	public static Byte safeValueOf( boolean i ) {
		return i
			?InvoiceStatus.SCORED.value()
			:InvoiceStatus.PENDING.value();
	}
	
	public static Byte safeValueOf( InvoiceStatus i ) {
		if (i == null) return null;
		return i.value();
	}
	public static InvoiceStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvoiceStatus.values().length) return null;
		return InvoiceStatus.values()[i];
	}
	
	public static InvoiceStatus safeValueOf( String str) {
		if(AonStringUtils.isBlank(str)) return PENDING;
		for (InvoiceStatus rs : values()) {
			if(rs.name().equalsIgnoreCase(str) || rs.getName().equalsIgnoreCase(str))
				return rs;
		}
		return PENDING;
	}
	
}
