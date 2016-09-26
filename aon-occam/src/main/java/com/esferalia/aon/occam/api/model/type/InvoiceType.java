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
		}),
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
		}),
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
		}),
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
		})
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
	
	private InvoiceType(String description,IInvoiceTypeVisitorWalker walker
			,IAccountingInvoiceTypeVisitorWalker accountingInvoiceWalker) {
		this.description = description;
		this.invoiceWalker = walker;
		this.accountingInvoiceWalker = accountingInvoiceWalker;
	}

	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getDescription() {
		return description;
	}
	public void visit(Invoice invoice, IInvoiceTypeVisitor visitor) {
		invoiceWalker.visit(invoice,visitor);
	}
	public void visit(AccountingInvoice invoice, IAccountingInvoiceTypeVisitor visitor) {
		accountingInvoiceWalker.visit(invoice,visitor);
	}
}
