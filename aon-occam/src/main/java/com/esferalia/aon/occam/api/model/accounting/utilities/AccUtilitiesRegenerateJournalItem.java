package com.esferalia.aon.occam.api.model.accounting.utilities;

import com.esferalia.aon.occam.api.model.AccountPeriod;

public class AccUtilitiesRegenerateJournalItem implements IAccUtilitiesItem {
	
	private static final long serialVersionUID = -8812702540651915368L;
	private Integer domain;
	private String domainName;
	
	private AccountPeriod accountPeriod;
	private boolean regenerable;
	
	private String message;
	
	@Override
	public AccUtilitiesItemType getType() {
		return AccUtilitiesItemType.UNBALANCED_ENTRY;
	}
	@Override
	public Integer getDomain() {
		return domain;
	}
	public AccUtilitiesRegenerateJournalItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public AccUtilitiesRegenerateJournalItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	@Override
	public String getMessage() {
		return message;
	}
	public AccUtilitiesRegenerateJournalItem setMessage(String message) {
		this.message = message;
		return this;
	}
	public AccountPeriod getAccountPeriod() {
		return accountPeriod;
	}
	public AccUtilitiesRegenerateJournalItem setAccountPeriod(AccountPeriod period) {
		this.accountPeriod = period;
		return this;
	}
	public boolean isRegenerable() {
		return regenerable;
	}
	public AccUtilitiesRegenerateJournalItem setRegenerable(boolean regenerable) {
		this.regenerable = regenerable;
		return this;
	}
	
}
