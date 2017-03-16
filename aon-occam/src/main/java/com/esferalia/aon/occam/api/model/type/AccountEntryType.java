package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.IAccountEntryTypeVisitor;


public enum AccountEntryType  implements Serializable {
	
	OPENING ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitOpening(entry);
		}
	}),
	CLOSING ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitClosing(entry);
		}
	}),
	OPERATING ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitOperating(entry);
		}
	}),
	MANUAL ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitManual(entry);
		}
	}),
	SALES_INVOICE ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitSalesInvoice(entry);
		}
	}),
	PURCHASE_INVOICE ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitPurchaseInvoice(entry);
		}
	}),
	EXPENSE_INVOICE ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitExpenseInvoice(entry);
		}
	}),
	@Deprecated
	INVESTMENT_INVOICE ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitInvestmentInvoice(entry);
		}
	}),
	EXPENSES ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitExpenses(entry);
		}
	}),
	SALARY ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitSalary(entry);
		}
	}),
	TAX ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitTax(entry);
		}
	}),
	LOAN ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitLoan(entry);
		}
	}),
	@Deprecated
	LEASING ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitLeasing(entry);
		}
	}),
	PAYMENT ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitPayment(entry);
		}
	}),
	COLLECTION ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitCollection(entry);
		}
	}),
	@Deprecated
	STOCK_VARIATION ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitStockVariation(entry);
		}
	}),
	AMORTIZATION ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitAmortization(entry);
		}
	}),
	SOCIAL_INSURANCE ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitSocialInsurance(entry);
		}
	}),
	LOAN_FEE ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitLoanFee(entry);
		}
	}),
	@Deprecated
	LEASING_FEE ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitLeasingFee(entry);
		}
	}),
	RETURNED_PAYMENT ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitReturnedPayment(entry);
		}
	}),
	RETURNED_COLLECTION ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitReturnedCollection(entry);
		}
	}),
	SOCIAL_INSURANCE_ADJUST ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitSocialInsuranceAdjust(entry);
		}
	}),
	FINANCE ( new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitFinance(entry);
		}
	}),
	;

	public interface IAccountEntryTypeVisitorWalker {
		void visit( AccountEntry entry, IAccountEntryTypeVisitor visitor);
	}
	
	private IAccountEntryTypeVisitorWalker walker;
	
	private AccountEntryType( IAccountEntryTypeVisitorWalker walker ) {
		this.walker = walker;		
	}
	
	
	public Byte getValue() {
		return (byte) ordinal();
	}
	
	public boolean isInvoice() {
		return (this == AccountEntryType.SALES_INVOICE
			|| this == AccountEntryType.PURCHASE_INVOICE
			|| this == AccountEntryType.EXPENSE_INVOICE
			|| this == AccountEntryType.INVESTMENT_INVOICE);
	}
	
	public void visit( AccountEntry entry, IAccountEntryTypeVisitor visitor) {
		this.walker.visit(entry, visitor);
	}
}