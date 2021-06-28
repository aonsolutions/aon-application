package com.esferalia.aon.occam.api.model.task;



import java.util.Calendar;

public enum TaskPeriod {

	NONE(0, 0),
	DAILY(Calendar.DATE, 1),
	WEEKLY(Calendar.DATE, 7),
	BI_WEEKLY(Calendar.DATE, 14),
	MONTHLY(Calendar.MONTH, 1),
	BI_MONTHLY(Calendar.MONTH, 2),
	THREE_MONTHLY(Calendar.MONTH, 3),
	FOUR_MONTHLY(Calendar.MONTH, 4),
	HALF_YEARLY(Calendar.MONTH, 12),
	YEARLY(Calendar.YEAR, 1);

	private int value;
	private int field;
	
	private TaskPeriod(int field, int value) {
		this.field = field;
		this.value = value;
	}
		
    public byte value() {
    	return (byte) this.ordinal();
    }

	public int getValue() {
		return value;
	}

	public int getField() {
		return field;
	}
	
	public static TaskPeriod safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static TaskPeriod safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= TaskPeriod.values().length) return null;
		return TaskPeriod.values()[i];
	}
}