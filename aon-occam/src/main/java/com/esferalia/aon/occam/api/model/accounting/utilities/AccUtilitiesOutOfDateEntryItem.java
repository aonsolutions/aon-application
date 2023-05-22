package com.esferalia.aon.occam.api.model.accounting.utilities;

import java.util.Date;

public class AccUtilitiesOutOfDateEntryItem implements IAccUtilitiesItem {
	
	private static final long serialVersionUID = 1356166170131499571L;
	private Integer domain;
	private String domainName;
	
	private Integer entryId;
	private String message;
	private Date entryDate;
	
	@Override
	public AccUtilitiesItemType getType() {
		return AccUtilitiesItemType.OUT_OF_DATE_ENTRY;
	}
	@Override
	public Integer getDomain() {
		return domain;
	}
	public AccUtilitiesOutOfDateEntryItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public AccUtilitiesOutOfDateEntryItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	@Override
	public String getMessage() {
		return message;
	}
	public AccUtilitiesOutOfDateEntryItem setMessage(String message) {
		this.message = message;
		return this;
	}
	public Integer getEntryId() {
		return entryId;
	}
	public AccUtilitiesOutOfDateEntryItem setEntryId(Integer entryId) {
		this.entryId = entryId;
		return this;
	}
	public Date getEntryDate() {
		return entryDate;
	}
	public AccUtilitiesOutOfDateEntryItem setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
		return this;
	}
	
}
