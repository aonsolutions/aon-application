package com.esferalia.aon.occam.api.model.registry;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.InvoiceType;

public enum InvoiceRegistryType implements Serializable {
	
	 CREDITOR 	("Acreedor"	,InvoiceType.EXPENSES) 	{ @Override public <T> T visit(InvoiceRegistryTypeVisitor<T> visitor) {return visitor.visitCreditor();}}
	,SUPPLIER 	("Proveedor",InvoiceType.PURCHASE)	{ @Override public <T> T visit(InvoiceRegistryTypeVisitor<T> visitor) {return visitor.visitSupplier();}}
	,CUSTOMER 	("Cliente"	,InvoiceType.SALES)		{ @Override public <T> T visit(InvoiceRegistryTypeVisitor<T> visitor) {return visitor.visitCustomer();}}
	;
	
	private String description;
	private InvoiceType invoiceType;
	
	private InvoiceRegistryType(String description,InvoiceType invoiceType) {
		this.description = description;
		this.invoiceType = invoiceType;
	}
	
	public String getDescription() {
		return description;
	}
	public InvoiceType getInvoiceType() {
		return invoiceType;
	}
	
	public abstract <T> T visit(InvoiceRegistryTypeVisitor<T> visitor);
	
		
	public interface InvoiceRegistryTypeVisitor<T> {
		T visitCustomer();
		T visitCreditor();
		T visitSupplier();
	}
}
