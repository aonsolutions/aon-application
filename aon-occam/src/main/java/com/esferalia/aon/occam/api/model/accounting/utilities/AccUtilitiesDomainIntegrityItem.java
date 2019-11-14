package com.esferalia.aon.occam.api.model.accounting.utilities;

import com.esferalia.aon.occam.api.model.Account;

public class AccUtilitiesDomainIntegrityItem implements IAccUtilitiesItem {
	
	private static final long serialVersionUID = -8812702540651915368L;
	
	private Integer domain;
	private String domainName;
	
	private Account wrongAccount;
	private Account rightAccount;
	private int count;
	
	private String message;
	
	@Override
	public Integer getDomain() {
		return domain;
	}
	public AccUtilitiesDomainIntegrityItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public AccUtilitiesDomainIntegrityItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	@Override
	public AccUtilitiesItemType getType() {
		return AccUtilitiesItemType.DOMAIN_INTEGRITY;
	}
	@Override
	public String getMessage() {
		return message;
	}
	public AccUtilitiesDomainIntegrityItem setMessage(String message) {
		this.message = message;
		return this;
	}
	public Account getWrongAccount() {
		return wrongAccount;
	}
	public AccUtilitiesDomainIntegrityItem setWrongAccount(Account wrongAccount) {
		this.wrongAccount = wrongAccount;
		return this;
	}
	public Account getRightAccount() {
		return rightAccount;
	}
	public AccUtilitiesDomainIntegrityItem setRightAccount(Account rightAccount) {
		this.rightAccount = rightAccount;
		return this;
	}
	public int getCount() {
		return count;
	}
	public AccUtilitiesDomainIntegrityItem setCount(int count) {
		this.count = count;
		return this;
	}
	
}
