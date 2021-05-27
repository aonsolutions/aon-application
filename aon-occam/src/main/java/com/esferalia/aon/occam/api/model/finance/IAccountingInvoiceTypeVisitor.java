package com.esferalia.aon.occam.api.model.finance;

import com.esferalia.aon.occam.api.model.AccountingInvoice;

public interface IAccountingInvoiceTypeVisitor {
	void visitPurchase(AccountingInvoice invoice);
	void visitSales(AccountingInvoice invoice);
	void visitExpenses(AccountingInvoice invoice);
	void visitUndeductible(AccountingInvoice invoice);
}
