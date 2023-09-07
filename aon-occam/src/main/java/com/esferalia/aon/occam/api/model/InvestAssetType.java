package com.esferalia.aon.occam.api.model;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvestAssetType implements Serializable{

	PREMISES("Local"),
	OTHER_BUILDING("Otros Inmuebles"),
	MEANS_OF_TRANSPORT("Medios de Transporte"),
	FIXED_PHONE("Telefono Fijo"),
	CELLULAR_PHONE("Telefono Movil"),
	FAX("Fax"),
	FURNITURE("Mobiliario"),
	MACHINERY("Maquinaria"),
	COMPUTER_EQUIPMENT("Equipos Informaticos"),
	INSTALLATION("Instalacion"),
	ACCOUNT_GROUP_20_ASSET("Bienes Grupo 20 PGC"),
	ACCOUNT_GROUP_21_ASSET("Bienes Grupo 21 PGC"),
	ACCOUNT_GROUP_23_ASSET("Bienes Grupo 23 PGC"),
	BUILDING_PLOT("Solar");

	private String description;
	
	private InvestAssetType(String description) {
		this.description = description;
	}
	
	public String description(){
		return description;
	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static InvestAssetType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static InvestAssetType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvestAssetType.values().length) return null;
		return InvestAssetType.values()[i];
	}
	
	public static InvestAssetType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (InvestAssetType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
