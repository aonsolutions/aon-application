package net.aonsolutions.occam.api.constants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceType implements Serializable  {

	PURCHASE("Compras")			
		{ @Override public <R,T> R visit(InvoiceTypeVisitor<R,T> v, T t) { return v.visitPurchase(t);} }
	,SALES("Ventas")
		{ @Override public <R,T> R visit(InvoiceTypeVisitor<R,T> v, T t) { return v.visitSales(t);} }
	,EXPENSES("Gastos")
		{ @Override public <R,T> R visit(InvoiceTypeVisitor<R,T> v, T t) { return v.visitExpenses(t);} }
	,UNDEDUCTIBLE("Gt.NO Ded")
		{ @Override public <R,T> R visit(InvoiceTypeVisitor<R,T> v, T t) { return v.visitUndeductible(t);} }
	;

	private String description;
	
	private InvoiceType(String description) {
		this.description = description;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getDescription() {
		return description;
	}
	public String getAbbrDescription() {
		return AonStringUtils.substring(description,0,4);
	}
	
	public static Optional<InvoiceType> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( i.intValue() ); 
	}
	
	public static Optional<InvoiceType> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= InvoiceType.values().length) return Optional.empty();
		return Optional.of( InvoiceType.values()[i]);
	}
	
	public static Optional<InvoiceType> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}
	
	public abstract <R,T> R visit(InvoiceTypeVisitor<R,T> visitor, T t);
	public static interface InvoiceTypeVisitor<R,T> {
		 R visitPurchase( T t );
		 R visitSales( T t );
		 R visitExpenses( T t );
		 R visitUndeductible( T t );
	}
	
}

