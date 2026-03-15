package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum NoteType implements Serializable {
	
    UNKNOWN,
    OBSERVATION,
    MESSAGE,
    TRACKING,
    FACTURAE,
    EDI,
    CUSTOMER_STATUS,
    PAYROLL,
    FISCAL,
    ACCOUNTING,
    COMMERCIAL,
    MANAGEMENT,
    TREASURY,
    WAREHOUSE,
    GROUPWARE,
    MARKETING
    ;
	
	private NoteType() {}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static NoteType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static NoteType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= NoteType.values().length) return null;
		return NoteType.values()[i];
	}
	
	public static NoteType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (NoteType rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	
}
