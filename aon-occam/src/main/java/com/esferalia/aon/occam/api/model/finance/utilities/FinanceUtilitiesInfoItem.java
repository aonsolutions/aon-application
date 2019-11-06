package com.esferalia.aon.occam.api.model.finance.utilities;

public class FinanceUtilitiesInfoItem implements IFinanceUtilitiesItem {
	
	private static final long serialVersionUID = 2102970365562544855L;
	
	private Integer domain;
	private String domainName;
	
	private String message;
	
	@Override
	public Integer getDomain() {
		return domain;
	}
	public FinanceUtilitiesInfoItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public FinanceUtilitiesInfoItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	@Override
	public FinanceUtilitiesItemType getType() {
		return FinanceUtilitiesItemType.INFO_MESSAGE;
	}
	@Override
	public String getMessage() {
		return message;
	}
	public FinanceUtilitiesInfoItem setMessage(String message) {
		this.message = message;
		return this;
	}
}
