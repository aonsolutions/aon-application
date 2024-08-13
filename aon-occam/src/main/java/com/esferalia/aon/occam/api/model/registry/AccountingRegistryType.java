package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

public enum AccountingRegistryType implements Serializable {
	
	CREDITOR ("Acreedor",InvoiceType.EXPENSES, AccountEntryType.EXPENSE_INVOICE, "4100") {
		@Override
		public void visit(AccountingRegistryTypeVisitor visitor) {
			visitor.visitCreditor();
		}
	}
	,SUPPLIER ("Proveedor",InvoiceType.PURCHASE, AccountEntryType.PURCHASE_INVOICE, "4000") {
		@Override
		public void visit(AccountingRegistryTypeVisitor visitor) {
			visitor.visitSupplier();
		}
	}
	,CUSTOMER ("Cliente",InvoiceType.SALES, AccountEntryType.SALES_INVOICE, "4300") {
		@Override
		public void visit(AccountingRegistryTypeVisitor visitor) {
			visitor.visitCustomer();
		}
	}
	,UNDED_CREDITOR ("Acreedor",InvoiceType.UNDEDUCTIBLE, AccountEntryType.EXPENSE_INVOICE, "4100") {
		@Override
		public void visit(AccountingRegistryTypeVisitor visitor) {
			visitor.visitUndedCreditor();
		}
	}
	;
	
	private String description;
	private InvoiceType invoiceType;
	private AccountEntryType accountEntryType;
	private String accountPrefix;
	
	private AccountingRegistryType(String description,InvoiceType invoiceType, AccountEntryType accountEntryType, String accountPrefix) {
		this.description = description;
		this.invoiceType = invoiceType;
		this.accountEntryType = accountEntryType;
		this.accountPrefix = accountPrefix;  
	}
	public String getDescription() {
		return description;
	}
	public String getAccountPrefix() {
		return accountPrefix;
	}
	public InvoiceType getInvoiceType() {
		return invoiceType;
	}
	public AccountEntryType getAccountEntryType() {
		return accountEntryType;
	}
	
	public static AccountingRegistryType getFor(InvoiceType type) {
		if (type == null) return null;
		if (type == InvoiceType.EXPENSES) return AccountingRegistryType.CREDITOR;
		if (type == InvoiceType.UNDEDUCTIBLE) return AccountingRegistryType.CREDITOR;
		if (type == InvoiceType.SALES) return AccountingRegistryType.CUSTOMER;
		if (type == InvoiceType.PURCHASE) return AccountingRegistryType.SUPPLIER;
		return null;
	}
		
	public abstract void visit(AccountingRegistryTypeVisitor visitor);
	
	public interface AccountingRegistryTypeVisitor {
		void visitCustomer();
		void visitCreditor();
		void visitSupplier();
		void visitUndedCreditor();
	}
}
