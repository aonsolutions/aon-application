package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum IncomeStatus implements Serializable {
	PENDING,
	INVOICED;

	public byte value(){
		return (byte) this.ordinal();
	}
	
    public String getName() {
		return this.toString();
    }
    
	public static IncomeStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static IncomeStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= IncomeStatus.values().length) return null;
		return IncomeStatus.values()[i];
	}
}
