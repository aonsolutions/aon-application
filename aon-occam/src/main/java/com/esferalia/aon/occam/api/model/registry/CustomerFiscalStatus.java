package com.esferalia.aon.occam.api.model.registry;

public enum CustomerFiscalStatus {

	REGISTERED("Censado"),
	NOT_REGISTERED("No Censado"),
	NOT_IDENTIFIED("No Identificado");

	String description;
	private CustomerFiscalStatus(String description) {
		this.description = description;
	}

	public byte value() {
		return (byte) this.ordinal();
	}

	public static CustomerFiscalStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() );
	}
	
	public static CustomerFiscalStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= CustomerFiscalStatus.values().length) return null;
		return CustomerFiscalStatus.values()[i];
	}
}
