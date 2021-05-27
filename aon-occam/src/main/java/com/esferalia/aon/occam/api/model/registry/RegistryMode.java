package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

public enum RegistryMode implements Serializable {

	TARGET(),
	CUSTOMER(),
	SUPPLIER(),
	CREDITOR;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static RegistryMode safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static RegistryMode safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= RegistryMode.values().length) return null;
		return RegistryMode.values()[i];
	}

}