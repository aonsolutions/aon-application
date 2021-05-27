package com.esferalia.aon.occam.api.model.registry;

public interface IAccountingRegistryTypeVisitor {
	void visitCustomer(AccountingRegistry reg);
	void visitCreditor(AccountingRegistry reg);
	void visitSupplier(AccountingRegistry reg);
	void visitUndedCreditor(AccountingRegistry reg);
}
