package com.esferalia.aon.occam.api.model.accounting.utilities;

import com.esferalia.aon.occam.api.model.registry.AccountingRegistryType;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class AccUtilitiesAccountLinkItem implements IAccUtilitiesItem {
	
	private static final long serialVersionUID = -8812702540651915368L;
	private Integer domain;
	private String domainName;
	
	private AccUtilitiesItemType type;
	private AccountingRegistryType registryType;
	private Integer accountId;
	private Integer accountDomain;
	private String accountCode;
	private String accountDescripion;
	
	private Integer linkedId;
	private String linkedDescription;
	private boolean linkedInactive;
	
	private String message;

	
	@Override
	public Integer getDomain() {
		return domain;
	}
	public AccUtilitiesAccountLinkItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public AccUtilitiesAccountLinkItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	
	@Override
	public AccUtilitiesItemType getType() {
		return type;
	}
	public AccUtilitiesAccountLinkItem setType(AccUtilitiesItemType type) {
		this.type = type;
		return this;
	}

	public AccountingRegistryType getRegistryType() {
		return registryType;
	}
	public AccUtilitiesAccountLinkItem setRegistryType(AccountingRegistryType registryType) {
		this.registryType = registryType;
		return this;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	public AccUtilitiesAccountLinkItem setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public Integer getAccountId() {
		return accountId;
	}
	public AccUtilitiesAccountLinkItem setAccountId(Integer accountId) {
		this.accountId = accountId;
		return this;
	}
	public Integer getAccountDomain() {
		return accountDomain;
	}
	public AccUtilitiesAccountLinkItem setAccountDomain(Integer accountDomain) {
		this.accountDomain = accountDomain;
		return this;
	}
	public String getAccountCode() {
		return accountCode;
	}
	public AccUtilitiesAccountLinkItem setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}

	public String getAccountDescripion() {
		return accountDescripion;
	}
	public AccUtilitiesAccountLinkItem setAccountDescripion(String accountDescripion) {
		this.accountDescripion = accountDescripion;
		return this;
	}
	
	public Integer getLinkedId() {
		return linkedId;
	}
	public AccUtilitiesAccountLinkItem setLinkedId(Integer linkedId) {
		this.linkedId = linkedId;
		return this;
	}
	public String getLinkedDescription() {
		return linkedDescription;
	}
	public AccUtilitiesAccountLinkItem setLinkedDescription(String linkedDescription) {
		this.linkedDescription = linkedDescription;
		return this;
	}
	public boolean isLinkedInactive() {
		return linkedInactive;
	}
	public AccUtilitiesAccountLinkItem setLinkedInactive(boolean linkedInactive) {
		this.linkedInactive = linkedInactive;
		return this;
	}
	
	public boolean isParentAccount() {
		return getAccountDomain() != null && !AonNumberUtils.equals(getDomain(),getAccountDomain());
	}
}
