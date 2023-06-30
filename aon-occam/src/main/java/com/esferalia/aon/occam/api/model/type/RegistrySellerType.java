package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum RegistrySellerType {
	
    COMERCIAL,
    SOPORTE;
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
    
    public static RegistrySellerType safeValueOf( Byte i ) {
		if (i == null) return COMERCIAL;
		return safeValueOf( i.intValue() ); 
	}

	public static RegistrySellerType safeValueOf( Integer i ) {
		if (i == null) return COMERCIAL;
		if (i < 0 || i >= RegistrySellerType.values().length) return null;
		return RegistrySellerType.values()[i];
	}
	
	public static RegistrySellerType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return COMERCIAL;
		for (RegistrySellerType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return COMERCIAL;
	}

}
