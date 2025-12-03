package com.esferalia.aon.occam.api.model.warehouse;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum InventoryStatus implements Serializable{
	
	PROCESSED,
	OPENED;
	
	private InventoryStatus() {

	}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static InventoryStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static InventoryStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= InventoryStatus.values().length) return null;
		return InventoryStatus.values()[i];
	}
	
	public static InventoryStatus safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (InventoryStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
