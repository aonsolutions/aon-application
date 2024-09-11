package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.finance.EnumVisitors.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.IAccountingInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum InvoiceType implements Serializable  {

	PURCHASE("Compras", "recibida") {
		@Override public <T> T visit(Invoice invoice,IInvoiceTypeVisitor<T> visitor) {return visitor.visitPurchase(invoice);}
		@Override public void visit(AccountingInvoice invoice,IAccountingInvoiceTypeVisitor visitor) { visitor.visitPurchase(invoice);}
	}
	,SALES("Ventas", "emitida") {
		@Override public <T> T visit(Invoice invoice,IInvoiceTypeVisitor<T> visitor) {return visitor.visitSales(invoice);}
		@Override public void visit(AccountingInvoice invoice,IAccountingInvoiceTypeVisitor visitor) {visitor.visitSales(invoice);}
	}
	,EXPENSES("Gastos", "recibida") {
		@Override public <T> T visit(Invoice invoice,IInvoiceTypeVisitor<T> visitor) {return visitor.visitExpenses(invoice);}
		@Override public void visit(AccountingInvoice invoice,IAccountingInvoiceTypeVisitor visitor) {visitor.visitExpenses(invoice);}
	}
	,UNDEDUCTIBLE("Gt.NO Ded", "ticket") {
		@Override public <T> T visit(Invoice invoice,IInvoiceTypeVisitor<T> visitor) {return visitor.visitUndeductible(invoice);}
		@Override public void visit(AccountingInvoice invoice,IAccountingInvoiceTypeVisitor visitor) {visitor.visitUndeductible(invoice);}
	}
	;
	
	private String description;
	private String tediName;
	
	private InvoiceType(String description, String tediName) {
		this.description = description;
		this.tediName = tediName;
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
	
	public String getTediName() {
		return tediName;
	}
	
	public abstract <T> T visit(Invoice invoice, IInvoiceTypeVisitor<T> visitor);
	public abstract void visit(AccountingInvoice invoice, IAccountingInvoiceTypeVisitor visitor);
	
	public static InvoiceType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static InvoiceType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvoiceType.values().length) return null;
		return InvoiceType.values()[i];
	}
	
	public static InvoiceType safeValueOf( String i ) {
		if(AonStringUtils.isBlank(i)) return null;
		for (InvoiceType rs : values()) {
			if(rs.name().equalsIgnoreCase(i) || rs.getDescription().equalsIgnoreCase(i) 
					|| rs.getTediName().equalsIgnoreCase(i))
				return rs;
		}
		return null;
	}

	public static boolean contains(Byte[] types, InvoiceType type) {
		if (type == null) return false;
		return AonCollectionUtils.stream(types).anyMatch( t -> AonNumberUtils.equals(t, type.value()) );
	}
}
