package com.esferalia.aon.occam.api.model.accounting.utilities;

public class AccUtilitiesInfoItem implements IAccUtilitiesItem {
	
	private static final long serialVersionUID = -8812702540651915368L;
	private Integer domain;
	private String domainName;
	
	private String message;
	
	@Override
	public Integer getDomain() {
		return domain;
	}
	public AccUtilitiesInfoItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public AccUtilitiesInfoItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	@Override
	public AccUtilitiesItemType getType() {
		return AccUtilitiesItemType.INFO_MESSAGE;
	}
	@Override
	public String getMessage() {
		return message;
	}
	public AccUtilitiesInfoItem setMessage(String message) {
		this.message = message;
		return this;
	}
}
