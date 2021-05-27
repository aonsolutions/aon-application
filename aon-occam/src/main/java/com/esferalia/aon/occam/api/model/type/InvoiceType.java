package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.finance.IAccountingInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.IInvoiceTypeVisitor;
import com.esferalia.aon.occam.api.model.finance.Invoice;

public enum InvoiceType implements Serializable  {

	PURCHASE("Compras"
		,new IInvoiceTypeVisitorWalker() {
			@Override
			public void visit(Invoice invoice,IInvoiceTypeVisitor visitor) {
				visitor.visitPurchase(invoice);
			}
		}
		,new IAccountingInvoiceTypeVisitorWalker() {
			@Override
			public void visit(AccountingInvoice invoice,IAccountingInvoiceTypeVisitor visitor) {
				visitor.visitPurchase(invoice);
			}
		}, "recibida"),
	SALES("Ventas"
		,new IInvoiceTypeVisitorWalker() {
			@Override
			public void visit(Invoice invoice,IInvoiceTypeVisitor visitor) {
				visitor.visitSales(invoice);
			}
		}
		,new IAccountingInvoiceTypeVisitorWalker() {
			@Override
			public void visit(AccountingInvoice invoice,IAccountingInvoiceTypeVisitor visitor) {
				visitor.visitSales(invoice);
			}
		}, "emitida"),
	EXPENSES("Gastos"
		,new IInvoiceTypeVisitorWalker() {
			@Override
			public void visit(Invoice invoice,IInvoiceTypeVisitor visitor) {
				visitor.visitExpenses(invoice);
			}
		}
		,new IAccountingInvoiceTypeVisitorWalker() {
			@Override
			public void visit(AccountingInvoice invoice,IAccountingInvoiceTypeVisitor visitor) {
				visitor.visitExpenses(invoice);
			}
		}, "recibida"),
	UNDEDUCTIBLE("Gt.NO Ded"
		,new IInvoiceTypeVisitorWalker() {
			@Override
			public void visit(Invoice invoice,IInvoiceTypeVisitor visitor) {
				visitor.visitUndeductible(invoice);
			}
		}
		,new IAccountingInvoiceTypeVisitorWalker() {
			@Override
			public void visit(AccountingInvoice invoice,IAccountingInvoiceTypeVisitor visitor) {
				visitor.visitUndeductible(invoice);
			}
		}, "ticket")
	;

	public interface IInvoiceTypeVisitorWalker {
		void visit( Invoice invoice, IInvoiceTypeVisitor visitor);
	}
	
	public interface IAccountingInvoiceTypeVisitorWalker {
		void visit( AccountingInvoice invoice, IAccountingInvoiceTypeVisitor visitor);
	}
	
	private String description;
	private IInvoiceTypeVisitorWalker invoiceWalker;
	private IAccountingInvoiceTypeVisitorWalker accountingInvoiceWalker;
	private String tediName;
	
	private InvoiceType(String description,IInvoiceTypeVisitorWalker walker
			,IAccountingInvoiceTypeVisitorWalker accountingInvoiceWalker, String tediName) {
		this.description = description;
		this.invoiceWalker = walker;
		this.accountingInvoiceWalker = accountingInvoiceWalker;
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
	
	public void visit(Invoice invoice, IInvoiceTypeVisitor visitor) {
		invoiceWalker.visit(invoice,visitor);
	}
	
	public void visit(AccountingInvoice invoice, IAccountingInvoiceTypeVisitor visitor) {
		accountingInvoiceWalker.visit(invoice,visitor);
	}
	
	public static InvoiceType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	
	public static InvoiceType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= InvoiceType.values().length) return null;
		return InvoiceType.values()[i];
	}
	
	public static InvoiceType safeValueOf( String value ) {
		if(value == null) return null;
		if(SALES.getDescription().equalsIgnoreCase(value) || SALES.name().equalsIgnoreCase(value)) {
			return SALES;
		} else if(PURCHASE.getDescription().equalsIgnoreCase(value) || PURCHASE.name().equalsIgnoreCase(value)) {
			return PURCHASE;
		} else if(EXPENSES.getDescription().equalsIgnoreCase(value) || EXPENSES.name().equalsIgnoreCase(value)) {
			return EXPENSES;
		} else if(UNDEDUCTIBLE.getDescription().equalsIgnoreCase(value) || UNDEDUCTIBLE.name().equalsIgnoreCase(value) ||"ticket".equalsIgnoreCase(value)) {
			return UNDEDUCTIBLE;
		}		
		return valueOf(value); 
	}
}
