package com.esferalia.aon.occam.api.model.accounting.utilities;

public class AccUtilitiesEmptyEntryItem implements IAccUtilitiesItem {
	
	private static final long serialVersionUID = -8812702540651915368L;
	private Integer domain;
	private String domainName;
	
	private Integer entryId;
	private String message;
	
	@Override
	public Integer getDomain() {
		return domain;
	}
	public AccUtilitiesEmptyEntryItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public AccUtilitiesEmptyEntryItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	@Override
	public AccUtilitiesItemType getType() {
		return AccUtilitiesItemType.EMPTY_ENTRY;
	}
	@Override
	public String getMessage() {
		return message;
	}
	public AccUtilitiesEmptyEntryItem setMessage(String message) {
		this.message = message;
		return this;
	}
	public Integer getEntryId() {
		return entryId;
	}
	public AccUtilitiesEmptyEntryItem setEntryId(Integer entryId) {
		this.entryId = entryId;
		return this;
	}
	
}
