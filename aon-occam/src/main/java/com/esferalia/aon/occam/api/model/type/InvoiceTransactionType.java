package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTransactionTypeVisitor;
import com.esferalia.aon.watson.util.AonStringUtils;

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

	public static InvoiceTransactionType safeValueOf( String str) {
		if (AonStringUtils.isBlank(str)) return NATIONAL;
		if("NAC".equalsIgnoreCase(str) || NATIONAL.name().equalsIgnoreCase(str) || NATIONAL.getDescription().equalsIgnoreCase(str)) {
			return NATIONAL;
		} else if("INTR".equalsIgnoreCase(str) || INTRACOMMUNITY.name().equalsIgnoreCase(str) || INTRACOMMUNITY.getDescription().equalsIgnoreCase(str)) {
			return INTRACOMMUNITY;
		} else if("EXTR".equalsIgnoreCase(str) || EXTRACOMMUNITY.name().equalsIgnoreCase(str) || EXTRACOMMUNITY.getDescription().equalsIgnoreCase(str)) {
			return EXTRACOMMUNITY;
		} else if("CCM".equalsIgnoreCase(str) || CAN_CEU_MEL.name().equalsIgnoreCase(str) || CAN_CEU_MEL.getDescription().equalsIgnoreCase(str)) {
			return CAN_CEU_MEL;
		} else if("ISP".equalsIgnoreCase(str) || OTHER_ISP.name().equalsIgnoreCase(str) || OTHER_ISP.getDescription().equalsIgnoreCase(str)) {
			return OTHER_ISP;
		} 
		return NATIONAL;
	}

	
	public void visit(IInvoiceTransactionTypeVisitor visitor) {
	}

}