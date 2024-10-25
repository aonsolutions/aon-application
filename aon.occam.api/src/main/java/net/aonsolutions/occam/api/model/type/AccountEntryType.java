package net.aonsolutions.occam.api.model.type;

import java.io.Serializable;
import java.util.Optional;

import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;


public enum AccountEntryType  implements Serializable {
	

	OPENING ("Apertura") 	
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitOpening();}},
	CLOSING ("Cierre") 		
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitClosing();}},
	OPERATING ("Explotaci\u00F3n") {
		@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitOperating();}},
	MANUAL ("Manual") {
		@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitManual();}},
	SALES_INVOICE ("Factura de Venta") {
		@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitSalesInvoice();}},
	PURCHASE_INVOICE ("Factura de Compra") {
		@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitPurchaseInvoice();}},
	EXPENSE_INVOICE ("Factura de Gastos") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitExpenseInvoice();}},
	@Deprecated
	INVESTMENT_INVOICE ("Factura de Inversi\u00F3n") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitManual();}},
	@Deprecated
	EXPENSES ("Gastos sin IVA") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitManual();}},
	SALARY ("N\u00F3minas") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitSalary();}},
	TAX ("Impuestos")
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitTax();}},
	LOAN ("Pr\u00E9stamos") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitLoan();}},
	@Deprecated
	LEASING ("Leasing") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitManual();}},
	PAYMENT ("Pago") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitPayment();}},
	COLLECTION ("Cobro") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitCollection();}},
	@Deprecated
	STOCK_VARIATION ("Variaci\u00F3n de Existencias") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitManual();}},
	AMORTIZATION ("Amortizaci\u00F3n") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitAmortization();}},
	SOCIAL_INSURANCE ( "Seg. Social") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitSocialInsurance();}},
	LOAN_FEE ("Cuotas Pr\u00E9stamos") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitLoanFee();}},
	@Deprecated
	LEASING_FEE ( "Cuotas Leasing") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitManual();}},
	RETURNED_PAYMENT ("Devoluci\u00F3n de Pago") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitReturnedPayment();}},
	RETURNED_COLLECTION ( "Devoluci\u00F3n de Cobro") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitReturnedCollection();}},
	SOCIAL_INSURANCE_ADJUST ( "Ajuste Seg. Social") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitSocialInsuranceAdjust();}},
	FINANCE ( "Tesorer\u00EDa") 
		{@Override public <T> T visit(AccountEntryTypeVisitor<T> visitor) {return visitor.visitFinance();}}
	;

	private String description;
	
	private AccountEntryType( String description) {
		this.description = description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public byte getValue() {
		return (byte) ordinal();
	}
	
	public boolean isInvoice() {
		return (this == AccountEntryType.SALES_INVOICE
			|| this == AccountEntryType.PURCHASE_INVOICE
			|| this == AccountEntryType.EXPENSE_INVOICE
			|| this == AccountEntryType.INVESTMENT_INVOICE);
	}
	
	public static Optional<AccountEntryType> value( Byte i ) {
		if (i == null) return Optional.empty();
		return value( i.intValue() ); 
	}
	
	public static Optional<AccountEntryType> value( Integer i ) {
		if (i == null) return Optional.empty();
		if (i < 0 || i >= AccountEntryType.values().length) return Optional.empty();
		return Optional.of(AccountEntryType.values()[i]);
	}
	
	public static Optional<AccountEntryType> value( String s ) {
		return AonCollectionUtils.stream(values())
			.filter( t ->  AonStringUtils.equalsIgnoreCase(t.name(), s))
			.findFirst();
	}
	
	public abstract <T> T visit(AccountEntryTypeVisitor<T> visitor);
	public interface AccountEntryTypeVisitor<T> {
		T visitOpening();
		T visitClosing();
		T visitOperating();
		T visitManual();
		T visitSalesInvoice();
		T visitPurchaseInvoice();
		T visitExpenseInvoice();
		T visitSalary();
		T visitTax();
		T visitLoan();
		T visitPayment();
		T visitCollection();
		T visitAmortization();
		T visitSocialInsurance();
		T visitLoanFee();
		T visitReturnedPayment();
		T visitReturnedCollection();
		T visitSocialInsuranceAdjust();
		T visitFinance();
	}
}