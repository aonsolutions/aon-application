package com.code.aon.ui.account.bridge.controller;


import java.util.List;

import com.code.aon.account.Account;
import com.code.aon.account.IAccount;
import com.code.aon.common.ITransferObject;
import com.code.aon.registry.IRegistry;

public class RegistryAccountCheckerTo implements ITransferObject{

	private static final long serialVersionUID = 6497333654050247745L;
	
	private IRegistry registry;
	private List<IAccount> accounts;
	
	public RegistryAccountCheckerTo(IRegistry registry) {
		this.registry = registry;	
	}	

	public IRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(IRegistry registry) {
		this.registry = registry;
	}

	public List<IAccount> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<IAccount> accounts) {
		this.accounts = accounts;
	}

	public boolean isWithoutAccount() {
		return (getAccounts() == null || getAccounts().isEmpty());
	}
	public boolean isMultipleAccount() {
		return (getAccounts() != null && getAccounts().size() > 1);
	}
	public boolean isSynchronizable() {
		if (!isWithoutAccount() && !isMultipleAccount() ) {
			IAccount ca = getAccounts().get(0); 
			Account a = ca.getAccount();
			return !(a.getDescription().equals( ca.getAccountDescription()));
		}
		return false;
	}
	public boolean isError() {
		return (isMultipleAccount() || isWithoutAccount() || isSynchronizable()); 		
	}
}
