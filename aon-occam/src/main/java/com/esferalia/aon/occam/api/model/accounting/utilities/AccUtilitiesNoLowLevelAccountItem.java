package com.esferalia.aon.occam.api.model.accounting.utilities;

import com.esferalia.aon.occam.api.model.Account;

public class AccUtilitiesNoLowLevelAccountItem implements IAccUtilitiesItem {
	
	private static final long serialVersionUID = -8812702540651915368L;
	private Integer domain;
	private String domainName;
	
	private Account account;
	private String message;
	
	@Override
	public Integer getDomain() {
		return domain;
	}
	public AccUtilitiesNoLowLevelAccountItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public AccUtilitiesNoLowLevelAccountItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	@Override
	public AccUtilitiesItemType getType() {
		return AccUtilitiesItemType.NO_LOW_LEVEL_ACCOUNT;
	}
	@Override
	public String getMessage() {
		return message;
	}
	public AccUtilitiesNoLowLevelAccountItem setMessage(String message) {
		this.message = message;
		return this;
	}
	public Account getAccount() {
		return account;
	}
	public AccUtilitiesNoLowLevelAccountItem setAccount(Account account) {
		this.account = account;
		return this;
	}
	
}
