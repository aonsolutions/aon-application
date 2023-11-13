package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum ElaborationSource {

	SALES;
	
	
	private ElaborationSource() {
		
	}
	
	public byte value(){
		return (byte) this.ordinal();
	}
    
    public static ElaborationSource safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static ElaborationSource safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ElaborationSource.values().length) return null;
		return ElaborationSource.values()[i];
	}

	public static ElaborationSource safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (ElaborationSource rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
