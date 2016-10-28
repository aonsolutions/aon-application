package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.AccountEntryType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;

public enum AccountingRegistryType implements Serializable {
	
	CREDITOR ("Acreedor",InvoiceType.EXPENSES, AccountEntryType.EXPENSE_INVOICE, "410"
			, new IAccountingRegistryTypeVisitorWalker() {
				@Override
				public void visit(AccountingRegistry reg,IAccountingRegistryTypeVisitor visitor) {
					visitor.visitCreditor(reg);
				}
			})
	,SUPPLIER ("Proveedor",InvoiceType.PURCHASE, AccountEntryType.PURCHASE_INVOICE, "400"
		,new IAccountingRegistryTypeVisitorWalker() {
			@Override
			public void visit(AccountingRegistry reg,IAccountingRegistryTypeVisitor visitor) {
				visitor.visitSupplier(reg);
			}
		})
	,CUSTOMER ("Cliente",InvoiceType.SALES, AccountEntryType.SALES_INVOICE, "430"
		, new IAccountingRegistryTypeVisitorWalker() {
			@Override
			public void visit(AccountingRegistry reg,IAccountingRegistryTypeVisitor visitor) {
				visitor.visitCustomer(reg);
			}
		})
	;
	
	public interface IAccountingRegistryTypeVisitorWalker {
		void visit( AccountingRegistry reg, IAccountingRegistryTypeVisitor visitor);
	}
	
	private String description;
	private InvoiceType invoiceType;
	private AccountEntryType accountEntryType;
	private String accountPrefix;
	private IAccountingRegistryTypeVisitorWalker walker;
	
	private AccountingRegistryType(String description,InvoiceType invoiceType, AccountEntryType accountEntryType
			, String accountPrefix,IAccountingRegistryTypeVisitorWalker walker) {
		this.description = description;
		this.invoiceType = invoiceType;
		this.accountEntryType = accountEntryType;
		this.accountPrefix = accountPrefix;  
		this.walker = walker;
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
	public void visit(AccountingRegistry reg, IAccountingRegistryTypeVisitor visitor) {
		walker.visit(reg,visitor);
	}
	
}
