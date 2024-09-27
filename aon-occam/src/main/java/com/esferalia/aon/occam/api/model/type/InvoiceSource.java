package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceSource implements Serializable {
	
	 DIRECT_EXPENSE	("Directa de gastos", (d,v) -> v.visitDirectExpense(d) )
	,PURCHASE		("Pedido de compra"	, (d,v) -> v.visitPurchase(d) )
	,SALES			("Pedido de venta"	, (d,v) -> v.visitSales(d) )
    ,DELIVERY		("Albarán de venta"	, (d,v) -> v.visitDelivery(d) )
    ,INCOME			("Albarán de compra", (d,v) -> v.visitIncome(d) )
    ,FEE			("Cuota"			, (d,v) -> v.visitFee(d) )
    ,ACCOUNT		("Apunte contable"	, (d,v) -> v.visitAccount(d) )			
    ,DIRECT_INVOICE	("Directa"			, (d,v) -> v.visitDirectInvoice(d) )
    ,OFFER			("Presupuesto"		, (d,v) -> v.visitOffer(d) )
    ,RESERVATION	("Reserva"			, (d,v) -> v.visitReservation(d) )
    ,TEDI 			("Tedi"				, (d,v) -> v.visitTedi(d) );

	public byte value() {
		return (byte) ordinal();
	}

	@FunctionalInterface
	public static interface IInvoiceSourceVisitorWalker extends Serializable {
		public void visit(InvoiceDetail detail, IInvoiceSourceVisitor visitor);
	}
	
	public static interface IInvoiceSourceVisitor extends Serializable{
		void visitDirectExpense(InvoiceDetail detail);
		void visitPurchase(InvoiceDetail detail);
		void visitSales(InvoiceDetail detail);
		void visitDelivery(InvoiceDetail detail);
		void visitIncome(InvoiceDetail detail);
		void visitFee(InvoiceDetail detail);
		void visitAccount(InvoiceDetail detail);
		void visitDirectInvoice(InvoiceDetail detail);
		void visitOffer(InvoiceDetail detail);
		void visitReservation(InvoiceDetail detail);
		void visitTedi(InvoiceDetail detail);
	}
	
	private String description;
	private IInvoiceSourceVisitorWalker walker;
	
	private InvoiceSource(String description, IInvoiceSourceVisitorWalker walker) {
		this.description = description;
		this.walker = walker;
	}
	
	public String getDescription() {
		return description;
	}
	public void visit(InvoiceDetail detail, IInvoiceSourceVisitor visitor) {
		this.walker.visit(detail, visitor);
	}
	
	public static InvoiceSource safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static InvoiceSource safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvoiceSource.values().length) return null;
		return InvoiceSource.values()[i];
	}
	
	public static InvoiceSource safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (InvoiceSource rs : values()) {
			if(i.equalsIgnoreCase(rs.name()))
				return rs;
		}
		return null;
	}
	public static String safeValueOf(InvoiceSource t) {
		return t == null ? null : t.name();
	}
}