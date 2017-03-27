package com.esferalia.aon.occam.api.model.type;

public enum ElaborationSource {

	SALES,
	PURCHASE,
	;
	
	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
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

}
