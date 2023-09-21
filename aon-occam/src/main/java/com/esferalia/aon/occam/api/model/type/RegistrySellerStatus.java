package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum RegistrySellerStatus {
	
    ACTIVE,
    INACTIVE;
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
    
    public static RegistrySellerStatus safeValueOf( Byte i ) {
		if (i == null) return ACTIVE;
		return safeValueOf( i.intValue() ); 
	}

	public static RegistrySellerStatus safeValueOf( Integer i ) {
		if (i == null) return ACTIVE;
		if (i < 0 || i >= RegistrySellerStatus.values().length) return null;
		return RegistrySellerStatus.values()[i];
	}
	
	public static RegistrySellerStatus safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return ACTIVE;
		for (RegistrySellerStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return ACTIVE;
	}

}
