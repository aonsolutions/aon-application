package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTransactionTypeVisitor;

public enum InvoiceTransactionType implements Serializable {

	NATIONAL ("Nacional")
		{ @Override public void visit(IInvoiceTransactionTypeVisitor visitor) { visitor.visitNational();} },
	INTRACOMMUNITY("Intracomunitaria")
		{ @Override public void visit(IInvoiceTransactionTypeVisitor visitor) { visitor.visitIntracommunity();} },
	EXTRACOMMUNITY("Extracomunitaria")
		{ @Override public void visit(IInvoiceTransactionTypeVisitor visitor) { visitor.visitExtracommunity();} },
	CAN_CEU_MEL("Canarias, Ceuta y Melilla")
		{ @Override public void visit(IInvoiceTransactionTypeVisitor visitor) { visitor.visitCanCeuMel();} },
	OTHER_ISP("I.S.P.")
		{ @Override public void visit(IInvoiceTransactionTypeVisitor visitor) { visitor.visitOtherISP();} },
	;
	
	private String description;
	
	private InvoiceTransactionType(String description) {
		this.description = description;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getDescription() {
		return description;
	}

	public static InvoiceTransactionType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static InvoiceTransactionType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvoiceTransactionType.values().length) return null;
		return InvoiceTransactionType.values()[i];
	}

	public void visit(IInvoiceTransactionTypeVisitor visitor) {
	}

}