package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public enum InvoiceSource implements Serializable {
	
	 DIRECT_EXPENSE	("Directa de gastos") {
		@Override public <T> T visit( InvoiceSourceVisitor<T> visitor ) {return visitor.visitDirectExpense();}
	 }
	,PURCHASE		("Pedido de compra"	) {
		@Override public <T> T visit( InvoiceSourceVisitor<T> visitor ) {return visitor.visitPurchase();}
	}
	,SALES			("Pedido de venta"	){
		@Override public <T> T visit( InvoiceSourceVisitor<T> visitor ) {return visitor.visitSales();}
	}
    ,DELIVERY		("Albarán de venta"	){
		@Override public <T> T visit( InvoiceSourceVisitor<T> visitor ) {return visitor.visitDelivery();}
	}
    ,INCOME			("Albarán de compra"){
		@Override public <T> T visit( InvoiceSourceVisitor<T> visitor ) {return visitor.visitIncome();}
	}
    ,FEE			("Cuota"			){
		@Override public <T> T visit( InvoiceSourceVisitor<T> visitor ) {return visitor.visitFee();}
	}
    ,ACCOUNT		("Apunte contable"	){
		@Override public <T> T visit( InvoiceSourceVisitor<T> visitor ) {return visitor.visitAccount();}
	}
    ,DIRECT_INVOICE	("Directa"			){
		@Override public <T> T visit( InvoiceSourceVisitor<T> visitor ) {return visitor.visitDirectInvoice();}
	}
    ,OFFER			("Presupuesto"		){
		@Override public <T> T visit( InvoiceSourceVisitor<T> visitor ) {return visitor.visitOffer();}
	}
    ,RESERVATION	("Reserva"			){
		@Override public <T> T visit( InvoiceSourceVisitor<T> visitor ) {return visitor.visitReservation();}
	}
    ,TEDI 			("Tedi"				){
		@Override public <T> T visit( InvoiceSourceVisitor<T> visitor ) {return visitor.visitTedi();}
	}
    ;
    
	private String description;
	
	private InvoiceSource(String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte value() {
		return (byte) ordinal();
	}

	public static Optional<InvoiceSource> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<InvoiceSource> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= InvoiceSource.values().length) return Optional.empty();
		return Optional.of(InvoiceSource.values()[i]);
	}
	
	public static Optional<InvoiceSource> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
	public static Byte value(InvoiceSource t) {
		return t == null ? null : t.value();
	}
	
	public abstract <T> T visit( InvoiceSourceVisitor<T> visitor );
	public static interface InvoiceSourceVisitor<T> {
		T visitDirectExpense();
		T visitPurchase();
		T visitSales();
		T visitDelivery();
		T visitIncome();
		T visitFee();
		T visitAccount();
		T visitDirectInvoice();
		T visitOffer();
		T visitReservation();
		T visitTedi();
	}
}