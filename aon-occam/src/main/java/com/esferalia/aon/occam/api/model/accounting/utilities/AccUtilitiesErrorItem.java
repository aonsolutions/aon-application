package com.esferalia.aon.occam.api.model.accounting.utilities;

public class AccUtilitiesErrorItem implements IAccUtilitiesItem {
	
	private static final long serialVersionUID = -8812702540651915368L;
	private Integer domain;
	private String domainName;
	
	private String message;
	
	public AccUtilitiesErrorItem() {
	}
	@Override
	public Integer getDomain() {
		return domain;
	}
	public AccUtilitiesErrorItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public AccUtilitiesErrorItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	@Override
	public AccUtilitiesItemType getType() {
		return AccUtilitiesItemType.ERROR_MESSAGE;
	}
	@Override
	public String getMessage() {
		return message;
	}
	public AccUtilitiesErrorItem setMessage(String message) {
		this.message = message;
		return this;
	}
}
