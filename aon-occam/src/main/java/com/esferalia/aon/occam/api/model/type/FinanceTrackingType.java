package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFinanceTrackingTypeVisitor;

public enum FinanceTrackingType {
	BATCHED("Remesado", FinanceStatus.BATCHED) 
		{ @Override public void visit(IFinanceTrackingTypeVisitor visitor) { visitor.visitBatched();} },
	PAID("Pagado", FinanceStatus.PAID )
		{ @Override public void visit(IFinanceTrackingTypeVisitor visitor) { visitor.visitPaid();} },
	RETURNED("Devuelto", FinanceStatus.RETURNED)
		{ @Override public void visit(IFinanceTrackingTypeVisitor visitor) { visitor.visitReturned();} },
    FRACTIONED("Fraccionado", FinanceStatus.PENDING)
    	{ @Override public void visit(IFinanceTrackingTypeVisitor visitor) { visitor.visitFractioned();} },
    SETTLED("Saldado", FinanceStatus.SETTLED)
		{ @Override public void visit(IFinanceTrackingTypeVisitor visitor) { visitor.visitSettled();} },
    ;
	
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

	public void visit(IFinanceTrackingTypeVisitor visitor) {
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