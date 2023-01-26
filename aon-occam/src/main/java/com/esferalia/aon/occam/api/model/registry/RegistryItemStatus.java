package com.esferalia.aon.occam.api.model.registry;

public enum RegistryItemStatus {
	ACTIVE,
	INTERESTED,
	REFUSED,
	INACTIVE;
	
	private RegistryItemStatus() {
		
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static RegistryItemStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static RegistryItemStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= RegistryItemStatus.values().length) return null;
		return RegistryItemStatus.values()[i];
	}
}
