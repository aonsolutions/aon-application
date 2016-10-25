package com.esferalia.aon.occam.api.model;

public class AccountEntryTypeVisitorAdapter implements IAccountEntryTypeVisitor {

	@Override public void visitOpening(AccountEntry entry) {}
	@Override public void visitClosing(AccountEntry entry) {}
	@Override public void visitOperating(AccountEntry entry) {}
	@Override public void visitManual(AccountEntry entry) {}
	@Override public void visitSalesInvoice(AccountEntry entry) {}
	@Override public void visitPurchaseInvoice(AccountEntry entry) {}
	@Override public void visitExpenseInvoice(AccountEntry entry) {}
	@Override public void visitInvestmentInvoice(AccountEntry entry) {}
	@Override public void visitExpenses(AccountEntry entry) {}
	@Override public void visitSalary(AccountEntry entry) {}
	@Override public void visitTax(AccountEntry entry) {}
	@Override public void visitLoan(AccountEntry entry) {}
	@Override public void visitPayment(AccountEntry entry) {}
	@Override public void visitCollection(AccountEntry entry) {}
	@Override public void visitStockVariation(AccountEntry entry) {}
	@Override public void visitAmortization(AccountEntry entry) {}
	@Override public void visitSocialInsurance(AccountEntry entry) {}
	@Override public void visitLoanFee(AccountEntry entry) {}
	@Override public void visitReturnedPayment(AccountEntry entry) {}
	@Override public void visitReturnedCollection(AccountEntry entry) {}
	@Override public void visitSocialInsuranceAdjust(AccountEntry entry) {}
	@Override public void visitLeasing(AccountEntry entry) {}
	@Override public void visitLeasingFee(AccountEntry entry) {}

}
