package com.esferalia.aon.occam.api.model.registry;


public enum RegistryType {
	CREDITOR,
	CUSTOMER,
	SUPPLIER;

	public static RegistryType  safeValueOf( String i ) {
		for (RegistryType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
