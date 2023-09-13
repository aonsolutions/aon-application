package com.esferalia.aon.occam.api.model.registry;

public enum BookingStatus {
	BILLABLE,
	NOT_BILLABLE,
	NOT_CONTRACTABLE,
	INACTIVE;
	
	private BookingStatus() {
		
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static BookingStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static BookingStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= BookingStatus.values().length) return null;
		return BookingStatus.values()[i];
	}
}
