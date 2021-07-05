package com.esferalia.aon.occam.api.model.accounting.utilities;

import com.esferalia.aon.occam.api.model.Account;
import com.esferalia.aon.occam.api.model.type.AccountDependency;

public class AccUtilitiesAccountChangeItem implements IAccUtilitiesItem {
	
	private static final long serialVersionUID = -7875753310340952061L;
	
	private Integer domain;
	private String domainName;
	private String message;
	private boolean selected;

	private AccountDependency dependency;
	private Account oldAccount;
	private Account newAccount;
	private Integer id;
	
	@Override
	public AccUtilitiesItemType getType() {
		return AccUtilitiesItemType.ACCOUNT_CHANGE;
	}
	@Override
	public Integer getDomain() {
		return domain;
	}
	public AccUtilitiesAccountChangeItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public AccUtilitiesAccountChangeItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	@Override
	public String getMessage() {
		return message;
	}
	public AccUtilitiesAccountChangeItem setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public boolean isSelected() {
		return selected;
	}
	public AccUtilitiesAccountChangeItem setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	public AccountDependency getDependency() {
		return dependency;
	}
	public AccUtilitiesAccountChangeItem setDependency(AccountDependency dependency) {
		this.dependency = dependency;
		return this;
	}
	
	public Account getOldAccount() {
		return oldAccount;
	}
	public AccUtilitiesAccountChangeItem setOldAccount(Account oldAccount) {
		this.oldAccount = oldAccount;
		return this;
	}
	
	public Account getNewAccount() {
		return newAccount;
	}
	public AccUtilitiesAccountChangeItem setNewAccount(Account newAccount) {
		this.newAccount = newAccount;
		return this;
	}

	public Integer getId() {
		return id;
	}
	public AccUtilitiesAccountChangeItem setId(Integer id) {
		this.id = id;
		return this;
	}
	
}
