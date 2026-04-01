package com.esferalia.aon.occam.api.model.registry;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IAccount;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;

public class SupplierFull extends RegistryFull<Supplier> implements IAccount<SupplierFull> {

	private static final long serialVersionUID = 7590291870827018067L;
	
	private Account account;
	
	@Override
	public Account getAccount() {
		return account;
	}
	@Override
	public SupplierFull setAccount(Account account) {
		this.account = account;
		ensureSupplier().setAccount(account==null?null:account.getId());
		return this;
	}
	public Supplier ensureSupplier() {
		if (getRegistry() == null) {
			setRegistry(new Supplier());
		}
		return getRegistry();
	}
	
	public static SupplierFull initialize(int domain) {
		SupplierFull full = new SupplierFull();
		full.setRegistry(new Supplier());
		full.getRegistry()
			.setDomain(new Domain().setId(domain))
			.setDocumentType(DocumentType.CIF)
			.setDocumentCountry(Country.ES)
			.setNationality(Country.ES);
		full.getRegistry().setStatus(RegistryStatus.ACTIVE);
		full.setAccount(new Account());
		full.initializeChilds();
		return full;
	}
	
}
