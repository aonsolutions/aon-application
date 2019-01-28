package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum PurchaseDetailStatus implements Serializable {
	
	PENDING,
	PARTIAL_SETTLED,
	SETTLED;
	
	public Byte value(){
		return (byte) this.ordinal();
	}

	public static PurchaseDetailStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static PurchaseDetailStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= PurchaseDetailStatus.values().length) return null;
		return PurchaseDetailStatus.values()[i];
	}
	
}
