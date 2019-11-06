package com.esferalia.aon.occam.api.model.finance.utilities;

public class FinanceUtilitiesErrorItem implements IFinanceUtilitiesItem {
	
	private static final long serialVersionUID = -8812702540651915368L;
	private Integer domain;
	private String domainName;
	
	private String message;
	
	public FinanceUtilitiesErrorItem() {
	}
	@Override
	public Integer getDomain() {
		return domain;
	}
	public FinanceUtilitiesErrorItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public FinanceUtilitiesErrorItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	@Override
	public FinanceUtilitiesItemType getType() {
		return FinanceUtilitiesItemType.ERROR_MESSAGE;
	}
	@Override
	public String getMessage() {
		return message;
	}
	public FinanceUtilitiesErrorItem setMessage(String message) {
		this.message = message;
		return this;
	}
}
