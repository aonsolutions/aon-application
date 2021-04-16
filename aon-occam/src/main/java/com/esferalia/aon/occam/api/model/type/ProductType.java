package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum ProductType implements Serializable {
	
	LABOUR("Mano de Obra"),
    SERVICE("Servicio"),
	COMMERCIAL_PRODUCT("Product Comercial"),
	EXTERNAL_WORK("Trabajo Externo"),
	EXPENSE("Gasto"),
	PREPAYMENT("Suplidos"),
	INCREASE("Recargo"),
	AUXILIARY("Auxiliar");

	String name;
	private ProductType(String name) {
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
	public static ProductType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ProductType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ProductType.values().length) return null;
		return ProductType.values()[i];
	}
	
	public static ProductType safeValueOf( String i ) {
		for (ProductType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()) || i.equalsIgnoreCase(rs.getName()))
				return rs;
		}
		return null;
	}
}
