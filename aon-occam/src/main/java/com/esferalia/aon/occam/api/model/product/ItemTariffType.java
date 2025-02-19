package com.esferalia.aon.occam.api.model.product;

import java.io.Serializable;

public enum ItemTariffType implements Serializable {
	
	FIXED("Fijo"),
    COST("Precio Coste"),
    SALE_BASE("Precio"),
    PVP("P.V.P.")
    ;
	
	private String description;
	
	private ItemTariffType(String description) {
		this.description = description;
	}

	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
	    return this.toString();
	}
    
    public String getDescription() {
	    return this.description;
	}
    
	public static ItemTariffType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static ItemTariffType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ItemTariffType.values().length) return null;
		return ItemTariffType.values()[i];
	}
	
	public static ItemTariffType safeValueOf( String i ) {
		if(i == null) return null;
		for (ItemTariffType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
	public static ItemTariffType safeValueOfDescription( String description ) {
		if(description == null) return null;
		for (ItemTariffType rs : values()) {
			if(description.equalsIgnoreCase(rs.getDescription()))
				return rs;
		}
		return null;
	}
	
}
