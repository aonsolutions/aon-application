package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IFinanceStatusVisitor;

public enum FinanceStatus implements Serializable {

	PENDING("Pendiente"){ @Override public void visit(IFinanceStatusVisitor visitor) { visitor.visitPending();} },
	BATCHED("Remesado" ){ @Override public void visit(IFinanceStatusVisitor visitor) { visitor.visitBatched();} },
	RETURNED("Devuelto"){ @Override public void visit(IFinanceStatusVisitor visitor) { visitor.visitReturned();} },
	PAID("Pagado"      ){ @Override public void visit(IFinanceStatusVisitor visitor) { visitor.visitPaid();} },
	SETTLED("Saldado"  ){ @Override public void visit(IFinanceStatusVisitor visitor) { visitor.visitSettled();} },
	;

	private String description;
	
	private FinanceStatus(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}

	public void visit(IFinanceStatusVisitor visitor) {
	}

	public static FinanceStatus safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static FinanceStatus safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= FinanceStatus.values().length) return null;
		return FinanceStatus.values()[i];
	}
}