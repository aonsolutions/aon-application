package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum OfferType implements Serializable {

	NORMAL("Normal")
	, INTERNET("Internet")
	, PROFORMA("Proforma")
	, DEALERSHIP("Representación")
	, OTHER("Otro");

	private String description;

	private OfferType(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public Byte value() {
		return (byte) ordinal();
	}
	
	public static OfferType safeValueOf( Byte i ) {
		if (i == null) return NORMAL;
		return safeValueOf( i.intValue() ); 
	}

	public static OfferType safeValueOf( Integer i ) {
		if (i == null) return NORMAL;
		if (i < 0 || i >= OfferType.values().length) return null;
		return OfferType.values()[i];
	}
	
	public static OfferType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return NORMAL;
		for (OfferType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()) || i.equalsIgnoreCase(rs.getDescription()))
				return rs;
		}
		return NORMAL;
	}
	
}
