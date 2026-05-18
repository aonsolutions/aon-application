package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum RegistrySource implements Serializable {
	
	ENVIROMENT,
	COMPANY,
	CUSTOMER,
	CREDITOR,
	SUPPLIER
    ;
	
	private RegistrySource() {}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static RegistrySource safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static RegistrySource safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= RegistrySource.values().length) return null;
		return RegistrySource.values()[i];
	}
	
	public static RegistrySource safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (RegistrySource rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
