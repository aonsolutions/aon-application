package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum RawdocNature implements Serializable {
	
	 INVOICE("Factura") {
		@Override public <T> T visit(RawdocNatureVisitor<T> visitor) {return visitor.visitInvoice(); }
	}
	,OTHER_INCOMES("Otros ingresos") {
		@Override public <T> T visit(RawdocNatureVisitor<T> visitor) {return visitor.visitOtherIncomes(); }
	}
	 /*
	  * NOMINA, PRESUPUESTO, PEDIDO, etc ....
	  */
	;

	private String description;
	
	private RawdocNature(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public static RawdocNature safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}

	public static RawdocNature safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= RawdocNature.values().length) return null;
		return RawdocNature.values()[i];
	}

	public static RawdocNature safeValueOf(String str) {
		if(AonStringUtils.isBlank(str)) return null;
		for (RawdocNature rs : values()) {
			if(str.equalsIgnoreCase(rs.name()) || str.equalsIgnoreCase(rs.getDescription()))
				return rs;
		}
		return null;		
	}	
	
	public abstract <T> T visit(RawdocNatureVisitor<T> visitor);
	
	public interface RawdocNatureVisitor<T> {
		T visitInvoice();
		T visitOtherIncomes();
	}
}
