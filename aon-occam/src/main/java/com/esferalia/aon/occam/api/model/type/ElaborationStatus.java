package com.esferalia.aon.occam.api.model.type;


public enum ElaborationStatus {

    PENDING("Pendiente"),
    CLOSED("Cerrado"),
    IN_PROGRESS("En progreso"),
    FAIL("Fallido"),
    REOPEN("Reabierto"),
    ;
	
	private String name;
	
	private ElaborationStatus(String name) {
		this.name = name;
	}
	
    public String getName() {
		return name;
    }
    
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static ElaborationStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static ElaborationStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= ElaborationStatus.values().length) return null;
		return ElaborationStatus.values()[i];
	}

}
