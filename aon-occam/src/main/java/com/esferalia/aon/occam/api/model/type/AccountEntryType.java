package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.IAccountEntryTypeVisitor;


public enum AccountEntryType  implements Serializable {
	

	OPENING ("Apertura", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitOpening(entry);
		}
	}),
	
	CLOSING ("Cierre", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitClosing(entry);
		}
	}),
	OPERATING ("Explotaci\u00F3n", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitOperating(entry);
		}
	}),
	MANUAL ("Manual",new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitManual(entry);
		}
	}),
	SALES_INVOICE ("Factura de Venta", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitSalesInvoice(entry);
		}
	}),
	PURCHASE_INVOICE ("Factura de Compra", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitPurchaseInvoice(entry);
		}
	}),
	EXPENSE_INVOICE ("Factura de Gastos", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitExpenseInvoice(entry);
		}
	}),
	@Deprecated
	INVESTMENT_INVOICE ("Factura de Inversi\u00F3n", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitInvestmentInvoice(entry);
		}
	}),
	EXPENSES ("Gastos sin IVA", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitExpenses(entry);
		}
	}),
	SALARY ("N\u00F3minas", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitSalary(entry);
		}
	}),
	TAX ("Impuestos", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitTax(entry);
		}
	}),
	LOAN ("Pr\u00E9stamos", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitLoan(entry);
		}
	}),
	@Deprecated
	LEASING ("Leasing", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitLeasing(entry);
		}
	}),
	PAYMENT ("Pago", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitPayment(entry);
		}
	}),
	COLLECTION ("Cobro", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitCollection(entry);
		}
	}),
	@Deprecated
	STOCK_VARIATION ( "Variaci\u00F3n de Existencias",new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitStockVariation(entry);
		}
	}),
	AMORTIZATION ("Amortizaci\u00F3n", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitAmortization(entry);
		}
	}),
	SOCIAL_INSURANCE ( "Seg. Social",new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitSocialInsurance(entry);
		}
	}),
	LOAN_FEE ("Cuotas Pr\u00E9stamos", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitLoanFee(entry);
		}
	}),
	@Deprecated
	LEASING_FEE ( "Cuotas Leasing",new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitLeasingFee(entry);
		}
	}),
	RETURNED_PAYMENT ("Devoluci\u00F3n de Pago", new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitReturnedPayment(entry);
		}
	}),
	RETURNED_COLLECTION ( "Devoluci\u00F3n de Cobro",new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitReturnedCollection(entry);
		}
	}),
	SOCIAL_INSURANCE_ADJUST ( "Ajuste Seg. Social",new  IAccountEntryTypeVisitorWalker() {
		@Override
		public void visit(AccountEntry entry, IAccountEntryTypeVisitor visitor) {
			visitor.visitSocialInsuranceAdjust(entry);
		}
	}),
	FINANCE ( "Tesorer\u00EDa", new  IAccountEntryTypeVisitorWalker() {
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
	private String description;
	
	private AccountEntryType( String description, IAccountEntryTypeVisitorWalker walker ) {
		this.description = description;
		this.walker = walker;		
	}
	
	public String getDescription() {
		return description;
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
	
	public static AccountEntryType safeValueOf( Byte i ) {
		if (i == null) return null;
		return safeValueOf( i.intValue() ); 
	}
	public static AccountEntryType safeValueOf( Integer i ) {
		if (i == null) return null;
		if (i < 0 || i >= AccountEntryType.values().length) return null;
		return AccountEntryType.values()[i];
	}
	
}