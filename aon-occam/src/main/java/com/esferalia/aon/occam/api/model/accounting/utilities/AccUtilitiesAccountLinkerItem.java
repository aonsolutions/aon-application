package com.esferalia.aon.occam.api.model.accounting.utilities;

import com.esferalia.aon.occam.api.model.Account;

public class AccUtilitiesAccountLinkerItem implements IAccUtilitiesItem {

	private static final long serialVersionUID = 5154772873334002479L;
	private Integer domain;
	private String domainName;
	
	private String message;
	private Account parentAccount;
	private Account childAccount;

	@Override
	public AccUtilitiesItemType getType() {
		return AccUtilitiesItemType.PARENT_ACCOUNT_LINKER;
	}
	@Override
	public Integer getDomain() {
		return domain;
	}
	public AccUtilitiesAccountLinkerItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public AccUtilitiesAccountLinkerItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	@Override
	public String getMessage() {
		return message;
	}
	public AccUtilitiesAccountLinkerItem setMessage(String message) {
		this.message = message;
		return this;
	}
	public Account getParentAccount() {
		return parentAccount;
	}
	public AccUtilitiesAccountLinkerItem setParentAccount(Account parentAccount) {
		this.parentAccount = parentAccount;
		return this;
	}
	public Account getChildAccount() {
		return childAccount;
	}
	public AccUtilitiesAccountLinkerItem setChildAccount(Account childAccount) {
		this.childAccount = childAccount;
		return this;
	}
	
}
