package com.esferalia.aon.occam.api.model.registry;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum RegistryType {
	CREDITOR,
	CUSTOMER,
	SUPPLIER;

	public static RegistryType  safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (RegistryType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
