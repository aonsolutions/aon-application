package com.esferalia.aon.occam.api.model.accounting.utilities;

public class AccUtilitiesRemoveEntryItem implements IAccUtilitiesItem {
	
	private static final long serialVersionUID = -8812702540651915368L;
	private Integer domain;
	private String domainName;
	
	private Integer entryId;
	private String message;
	
	@Override
	public Integer getDomain() {
		return domain;
	}
	public AccUtilitiesRemoveEntryItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public AccUtilitiesRemoveEntryItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	@Override
	public AccUtilitiesItemType getType() {
		return AccUtilitiesItemType.DELETE_ENTRY;
	}
	@Override
	public String getMessage() {
		return message;
	}
	public AccUtilitiesRemoveEntryItem setMessage(String message) {
		this.message = message;
		return this;
	}
	public Integer getEntryId() {
		return entryId;
	}
	public AccUtilitiesRemoveEntryItem setEntryId(Integer entryId) {
		this.entryId = entryId;
		return this;
	}
	
}
