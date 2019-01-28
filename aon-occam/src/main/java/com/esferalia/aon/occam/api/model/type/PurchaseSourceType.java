package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum PurchaseSourceType implements Serializable {

	PROPOSAL,
	PURCHASE,
	SALES;

	public byte value() {
		return (byte) ordinal();
	}
	
	public static PurchaseSourceType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static PurchaseSourceType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= PurchaseSourceType.values().length) return null;
		return PurchaseSourceType.values()[i];
	}
}