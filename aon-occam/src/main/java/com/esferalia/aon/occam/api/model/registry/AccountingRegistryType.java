package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

public enum AccountingRegistryType implements Serializable {
	
	SUPPLIER (InvoiceType.PURCHASE, AccountEntryType.PURCHASE_INVOICE
		,new IAccountingRegistryTypeVisitorWalker() {
			@Override
			public void visit(IAccountingRegistryTypeVisitor visitor) {
				visitor.visitSupplier();
			}
		})
	,CUSTOMER (InvoiceType.SALES, AccountEntryType.SALES_INVOICE
		, new IAccountingRegistryTypeVisitorWalker() {
			@Override
			public void visit(IAccountingRegistryTypeVisitor visitor) {
				visitor.visitCustomer();
			}
		})
	,CREDITOR (InvoiceType.EXPENSES, AccountEntryType.EXPENSE_INVOICE
		, new IAccountingRegistryTypeVisitorWalker() {
			@Override
			public void visit(IAccountingRegistryTypeVisitor visitor) {
				visitor.visitCreditor();
			}
		})
	;
	
	public interface IAccountingRegistryTypeVisitorWalker {
		void visit( IAccountingRegistryTypeVisitor visitor);
	}
	
	
	private InvoiceType invoiceType;
	private AccountEntryType accountEntryType;
	private IAccountingRegistryTypeVisitorWalker walker;
	
	private AccountingRegistryType(InvoiceType invoiceType, AccountEntryType accountEntryType
			,IAccountingRegistryTypeVisitorWalker walker) {
		this.invoiceType = invoiceType;
		this.accountEntryType = accountEntryType; 
		this.walker = walker;
	}
	
	
	public InvoiceType getInvoiceType() {
		return invoiceType;
	}
	public AccountEntryType getAccountEntryType() {
		return accountEntryType;
	}
	public void visit(IAccountingRegistryTypeVisitor visitor) {
		walker.visit(visitor);
	}
	
}
