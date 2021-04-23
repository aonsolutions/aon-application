package com.esferalia.aon.occam.api.model.aonsolutions;

public enum NotificationSource {
	DOCUMENTAL
	;
	
	private NotificationSource() {}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	
	public static Byte value(NotificationSource nc){
		return nc !=null ?  nc.value() : null;
	}
	
	
	public static NotificationSource safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static NotificationSource safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= NotificationSource.values().length) return null;
		return NotificationSource.values()[i];
	}
	
	public static NotificationSource safeValueOf( String i ) {
		for (NotificationSource rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
}
