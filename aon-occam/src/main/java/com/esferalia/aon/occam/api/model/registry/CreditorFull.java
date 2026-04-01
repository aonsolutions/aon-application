package com.esferalia.aon.occam.api.model.registry;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IAccount;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;
import com.esferalia.aon.occam.api.model.type.RegistryStatus;

public class CreditorFull extends RegistryFull<Creditor> implements IAccount<CreditorFull>{

	private static final long serialVersionUID = -6807179702470003551L;
	
	private Account account;
	
	@Override
	public Account getAccount() {
		return account;
	}
	@Override
	public CreditorFull setAccount(Account account) {
		this.account = account;
		ensureCreditor().setAccount(account==null?null:account.getId());
		return this;
	}
	public Creditor ensureCreditor() {
		if (getRegistry() == null) {
			setRegistry(new Creditor());
		}
		return getRegistry();
	}
	
	public static CreditorFull initialize(int domain) {
		CreditorFull full = new CreditorFull();
		full.setRegistry(new Creditor());
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
