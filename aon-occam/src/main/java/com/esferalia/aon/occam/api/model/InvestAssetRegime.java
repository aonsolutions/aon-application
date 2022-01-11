package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

public enum InvestAssetRegime implements Serializable{

	PROPERTY,
	RENTING,
	FINANCIAL_LEASING,
	OTHER;

	private InvestAssetRegime() {

	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static InvestAssetRegime safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static InvestAssetRegime safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvestAssetRegime.values().length) return null;
		return InvestAssetRegime.values()[i];
	}
	
	public static InvestAssetRegime safeValueOf( String i ) {
		for (InvestAssetRegime rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
