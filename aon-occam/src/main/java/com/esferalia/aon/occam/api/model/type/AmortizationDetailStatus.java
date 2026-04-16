package com.esferalia.aon.occam.api.model.type;

import java.util.Optional;

public enum AmortizationDetailStatus {
	
    PENDING	("Pendiente") 		{@Override public void accept(AmortizationDetailStatusVisitor visitor) {visitor.visitPending();}},
    SCORED 	("Contabilizado")	{@Override public void accept(AmortizationDetailStatusVisitor visitor) {visitor.visitScored();}},
    BLOCKED ("Bloqueado")		{@Override public void accept(AmortizationDetailStatusVisitor visitor) {visitor.visitBlocked();}}
    ;
	private final String description;
	private AmortizationDetailStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static Optional<AmortizationDetailStatus> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty(); ;
		return safeValueOf( i.intValue() ); 
	}
	public static Optional<AmortizationDetailStatus> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty(); ;
		if (i < 0 || i >= AmortizationDetailStatus.values().length) return Optional.empty();
		return Optional.of(AmortizationDetailStatus.values()[i]);
	}
	
	public abstract void accept( AmortizationDetailStatusVisitor visitor );
	public interface AmortizationDetailStatusVisitor {
		void visitPending();
		void visitScored();
		void visitBlocked();
	}
	
	
}
