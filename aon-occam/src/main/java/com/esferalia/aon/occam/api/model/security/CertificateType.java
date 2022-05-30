package com.esferalia.aon.occam.api.model.security;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum CertificateType {
	TGSS,
	SEPE,
	AEAT;
	
	private CertificateType() {
	
	}

	public Byte value(){
		return (byte) ordinal();
	}
	
	public static CertificateType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static CertificateType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= CertificateType.values().length) return null;
		return CertificateType.values()[i];
	}
	
	public static CertificateType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (CertificateType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
