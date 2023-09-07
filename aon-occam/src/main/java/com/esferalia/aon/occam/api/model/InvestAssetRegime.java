package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvestAssetRegime implements Serializable{

	PROPERTY("Propiedad"),
	RENTING("Alquiler"),
	FINANCIAL_LEASING("Arrendamiento Financiero"),
	OTHER("Otro");

	private String description;
	
	private InvestAssetRegime(String description) {
		this.description = description;
	}
	
	public String description() {
		return description;
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
		if(AonStringUtils.isBlank(i)) return null;
		for (InvestAssetRegime rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
