package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceTransactionType implements Serializable {

	NATIONAL ("Op. Interiores", "NAC")
		{ @Override public <T> T visit(InvoiceTransactionTypeVisitor<T> visitor){ return visitor.visitNational();} },
	INTRACOMMUNITY("Intracomunitaria", "INTR")
		{ @Override public <T> T visit(InvoiceTransactionTypeVisitor<T> visitor){ return visitor.visitIntracommunity();} },
	EXTRACOMMUNITY("Extracomunitaria", "EXTR")
		{ @Override public <T> T visit(InvoiceTransactionTypeVisitor<T> visitor){ return visitor.visitExtracommunity();} },
	CAN_CEU_MEL("Canarias, Ceuta y Melilla", "CCM")
		{ @Override public <T> T visit(InvoiceTransactionTypeVisitor<T> visitor){ return visitor.visitCanCeuMel();} },
	OTHER_ISP("I.S.P.", "ISP")
		{ @Override public <T> T visit(InvoiceTransactionTypeVisitor<T> visitor){ return visitor.visitOtherISP();} },
	;
	
	private String description;
	private String tediName;
	
	private InvoiceTransactionType(String description, String tediName) {
		this.description = description;
		this.tediName = tediName;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getTediName() {
		return tediName;
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
		if(AonStringUtils.isBlank(str)) return NATIONAL;
		for (InvoiceTransactionType rs : values()) {
			if(rs.name().equalsIgnoreCase(str) || rs.getTediName().equalsIgnoreCase(str) || rs.getDescription().equalsIgnoreCase(str))
				return rs;
		}
		return NATIONAL;
	}
	
	public static String name( InvoiceTransactionType t) {
		return (t == null) ? null : t.name();
	}

	
	public abstract <T> T visit(InvoiceTransactionTypeVisitor<T> visitor);
	public static interface InvoiceTransactionTypeVisitor<T> {
		T visitNational();
		T visitIntracommunity();
		T visitExtracommunity();
		T visitCanCeuMel();
		T visitOtherISP();
	}

}