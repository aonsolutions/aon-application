package com.esferalia.aon.occam.api.model.type;

public enum FinanceTrackingType {
	BATCHED("Remesado", FinanceStatus.BATCHED ),
	PAID("Pagado", FinanceStatus.PAID ),
	RETURNED("Devuelto", FinanceStatus.RETURNED),
    FRACTIONED("Fraccionado", FinanceStatus.PENDING),
    SETTLED("Saldado", FinanceStatus.SETTLED);
	
	private String description;
	private FinanceStatus financeStatus;
	
	private FinanceTrackingType(String description, FinanceStatus financeStatus) {
		this.description = description;
		this.financeStatus = financeStatus;
	}
	
	public String getDescription() {
		return description;
	}
	public FinanceStatus getFinanceStatus() {
		return this.financeStatus;
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