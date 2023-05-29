package net.aonsolutions.occam.api.constants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;

import net.aonsolutions.watson.client.util.AonStringUtils;

public enum InvoiceSource implements Serializable {
	
	 DIRECT_EXPENSE	("Directa de gastos")
		{ @Override public <R,T> R visit(InvoiceSourceVisitor<R,T> v, T t) { return v.visitDirectExpense(t);} }
	,PURCHASE		("Pedido de compra")
		{ @Override public <R,T> R visit(InvoiceSourceVisitor<R,T> v, T t) { return v.visitPurchase(t);} }
	,SALES			("Pedido de venta")
		{ @Override public <R,T> R visit(InvoiceSourceVisitor<R,T> v, T t) { return v.visitSales(t);} }
    ,DELIVERY		("Albarán de venta")
		{ @Override public <R,T> R visit(InvoiceSourceVisitor<R,T> v, T t) { return v.visitDelivery(t);} }
    ,INCOME			("Albarán de compra")
		{ @Override public <R,T> R visit(InvoiceSourceVisitor<R,T> v, T t) { return v.visitIncome(t);} }
    ,FEE			("Cuota")
		{ @Override public <R,T> R visit(InvoiceSourceVisitor<R,T> v, T t) { return v.visitFee(t);} }
    ,ACCOUNT		("Apunte contable")
		{ @Override public <R,T> R visit(InvoiceSourceVisitor<R,T> v, T t) { return v.visitAccount(t);} }
    ,DIRECT_INVOICE	("Directa")
    	{ @Override public <R,T> R visit(InvoiceSourceVisitor<R,T> v, T t) { return v.visitDirectInvoice(t);} }
    ,OFFER			("Presupuesto")
		{ @Override public <R,T> R visit(InvoiceSourceVisitor<R,T> v, T t) { return v.visitOffer(t);} }
    ,RESERVATION	("Reserva")
		{ @Override public <R,T> R visit(InvoiceSourceVisitor<R,T> v, T t) { return v.visitReservation(t);} }
    ,TEDI 			("Tedi")
		{ @Override public <R,T> R visit(InvoiceSourceVisitor<R,T> v, T t) { return v.visitTedi(t);} }
    ;

	public byte value() {
		return (byte) ordinal();
	}

	private String description;
	
	private InvoiceSource(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}

	public static Optional<InvoiceSource> safeValueOf( Byte i ) {
		if (i == null) return Optional.empty();
		return safeValueOf( i.intValue() ); 
	}
	
	public static Optional<InvoiceSource> safeValueOf( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= InvoiceSource.values().length) return Optional.empty();
		return Optional.of( InvoiceSource.values()[i]);
	}
	
	public static Optional<InvoiceSource> safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return Optional.empty();
		return Arrays.stream(values())
			.filter(dt -> i.equalsIgnoreCase(dt.name()))
			.findFirst();
	}
	
	public abstract <R,T> R visit(InvoiceSourceVisitor<R,T> visitor, T t);
	public static interface InvoiceSourceVisitor<R,T> {
		R visitDirectExpense(T t);
		R visitPurchase(T t);
		R visitSales(T t);
		R visitDelivery(T t);
		R visitIncome(T t);
		R visitFee(T t);
		R visitAccount(T t);
		R visitDirectInvoice(T t);
		R visitOffer(T t);
		R visitReservation(T t);
		R visitTedi(T t);
	}
}