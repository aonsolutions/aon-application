package com.esferalia.aon.occam.api.model;

public interface IAccountEntryTypeVisitor {

	void visitOpening(AccountEntry entry);
	void visitClosing(AccountEntry entry);
	void visitOperating(AccountEntry entry);
	void visitManual(AccountEntry entry);
	void visitSalesInvoice(AccountEntry entry);
	void visitPurchaseInvoice(AccountEntry entry);
	void visitExpenseInvoice(AccountEntry entry);
	void visitInvestmentInvoice(AccountEntry entry);
	void visitExpenses(AccountEntry entry);
	void visitSalary(AccountEntry entry);
	void visitTax(AccountEntry entry);
	void visitLoan(AccountEntry entry);
	void visitPayment(AccountEntry entry);
	void visitCollection(AccountEntry entry);
	void visitStockVariation(AccountEntry entry);
	void visitAmortization(AccountEntry entry);
	void visitSocialInsurance(AccountEntry entry);
	void visitLoanFee(AccountEntry entry);
	void visitReturnedPayment(AccountEntry entry);
	void visitReturnedCollection(AccountEntry entry);
	void visitSocialInsuranceAdjust(AccountEntry entry);
	void visitFinance(AccountEntry entry);
	@Deprecated
	void visitLeasing(AccountEntry entry);
	@Deprecated
	void visitLeasingFee(AccountEntry entry);

}
