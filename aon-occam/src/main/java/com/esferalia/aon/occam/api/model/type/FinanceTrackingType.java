package com.esferalia.aon.occam.api.model.type;

public enum FinanceTrackingType {
	BATCHED("Remesado"),
	PAID("Pagado"),
	RETURNED("Devuelto"),
    FRACTIONED("Fraccionado"),
    SETTLED("Saldado");
	
	private String description;
	
	private FinanceTrackingType(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}

	public static FinanceTrackingType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static FinanceTrackingType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= FinanceTrackingType.values().length) return null;
		return FinanceTrackingType.values()[i];
	}
	
}