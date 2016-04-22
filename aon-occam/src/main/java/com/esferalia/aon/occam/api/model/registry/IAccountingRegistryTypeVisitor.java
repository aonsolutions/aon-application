package com.esferalia.aon.occam.api.model.registry;

public interface IAccountingRegistryTypeVisitor {
	void visitCustomer();
	void visitCreditor();
	void visitSupplier();
}
