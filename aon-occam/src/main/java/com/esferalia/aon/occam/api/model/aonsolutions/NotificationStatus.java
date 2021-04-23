package com.esferalia.aon.occam.api.model.aonsolutions;

public enum NotificationStatus {
	UNREAD,
	READ
	;
	
	private NotificationStatus() {}
	
	public Byte value(){
		return (byte) ordinal();
	}
	
	public static Byte value(NotificationStatus ns){
		return ns !=null ?  ns.value() : 0;
	}
	
	public static NotificationStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static NotificationStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= NotificationStatus.values().length) return null;
		return NotificationStatus.values()[i];
	}
	
	public static NotificationStatus safeValueOf( String i ) {
		for (NotificationStatus rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return NotificationStatus.UNREAD;
	}
}
