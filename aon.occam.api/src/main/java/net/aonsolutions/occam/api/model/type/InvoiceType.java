package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceType implements Serializable  {

	PURCHASE("Compras") {
		@Override public <T> T visit( InvoiceTypeVisitor<T> visitor ) {return visitor.visitPurchase();}
	}
	,SALES("Ventas") {
		@Override public <T> T visit( InvoiceTypeVisitor<T> visitor ) {return visitor.visitSales();}
	}
	,EXPENSES("Gastos") {
		@Override public <T> T visit( InvoiceTypeVisitor<T> visitor ) {return visitor.visitExpenses();}
	}
	,UNDEDUCTIBLE("No Ded.") {
		@Override public <T> T visit( InvoiceTypeVisitor<T> visitor ) {return visitor.visitUndeductible();}
	}
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
	
	public static Optional<InvoiceType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<InvoiceType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= InvoiceType.values().length) return Optional.empty();
		return Optional.of(InvoiceType.values()[i]);
	}
	
	public static Optional<InvoiceType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
	public static Byte value(InvoiceType t) {
		return t == null ? null : t.value();
	}
	public static String name(InvoiceType t) {
		return t == null ? null : t.name();
	}
	
	
	public abstract <T> T visit( InvoiceTypeVisitor<T> visitor );
	public static interface InvoiceTypeVisitor<T> {
		T visitPurchase();
		T visitSales();
		T visitExpenses();
		T visitUndeductible();
	}
	
}
