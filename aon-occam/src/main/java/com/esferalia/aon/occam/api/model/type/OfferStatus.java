package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum OfferStatus implements Serializable {

	PENDING("Pendiente")
	, APPROVED("Aprobado")
	, REFUSED("Rechazado")
	, BLOCKED("Bloqueado")
	, INVOICED("Facturado");

	private String description;

	private OfferStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public Byte value() {
		return (byte) ordinal();
	}
	
	public static OfferStatus safeValueOf( Byte i ) {
		if (i == null) return PENDING;
		return safeValueOf( i.intValue() ); 
	}

	public static OfferStatus safeValueOf( Integer i ) {
		if (i == null) return PENDING;
		if (i < 0 || i >= OfferStatus.values().length) return null;
		return OfferStatus.values()[i];
	}
	
	public static OfferStatus safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return PENDING;
		for (OfferStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()) || i.equalsIgnoreCase(rs.getDescription()))
				return rs;
		}
		return PENDING;
	}

}