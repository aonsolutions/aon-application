package com.esferalia.aon.occam.api.model.finance;

public interface IInvoiceTypeVisitor {
	void visitPurchase(Invoice invoice);
	void visitSales(Invoice invoice);
	void visitExpenses(Invoice invoice);
	void visitUndeductible(Invoice invoice);
}
