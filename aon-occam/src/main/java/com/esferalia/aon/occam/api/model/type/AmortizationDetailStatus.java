package com.esferalia.aon.occam.api.model.type;

import java.util.Optional;

public enum AmortizationDetailStatus {
	
    PENDING,
    SCORED,
    BLOCKED;
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static Optional<AmortizationDetailStatus> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty(); ;
		return safeValueOf( i.intValue() ); 
	}
	public static Optional<AmortizationDetailStatus> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty(); ;
		if (i < 0 || i >= AmortizationDetailStatus.values().length) return null;
		return Optional.of(AmortizationDetailStatus.values()[i]);
	}
}
