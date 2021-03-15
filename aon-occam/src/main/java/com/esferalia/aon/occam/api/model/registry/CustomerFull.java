package com.esferalia.aon.occam.api.model.registry;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.Customer;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.IAccount;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.DocumentType;

public class CustomerFull extends RegistryFull<Customer> implements IAccount<CustomerFull>{

	private static final long serialVersionUID = -2437412710996159887L;
	
	private Account account;
	
	@Override
	public Account getAccount() {
		return account;
	}
	@Override
	public CustomerFull setAccount(Account account) {
		this.account = account;
		ensureCustomer().setAccount(account==null?null:account.getId());
		return this;
	}
	public Customer ensureCustomer() {
		if (getRegistry() == null) {
			setRegistry(new Customer());
		}
		return getRegistry();
	}
	public static CustomerFull initialize(int domain) {
		CustomerFull full = new CustomerFull();
		full.setRegistry(new Customer());
		full.getRegistry()
		.setDomain(new Domain().setId(domain))
		.setDocumentType(DocumentType.CIF)
		.setDocumentCountry(Country.ES)
		.setNationality(Country.ES);
		full.setAccount(new Account());
		full.initializeChilds();
		return full;
	}
	
}
