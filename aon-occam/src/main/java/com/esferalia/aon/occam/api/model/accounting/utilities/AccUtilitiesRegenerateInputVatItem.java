package com.esferalia.aon.occam.api.model.accounting.utilities;

import com.esferalia.aon.occam.api.model.AccountPeriod;

public class AccUtilitiesRegenerateInputVatItem implements IAccUtilitiesItem {
	
	private static final long serialVersionUID = -8812702540651915368L;
	private Integer domain;
	private String domainName;
	
	private Integer year;
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
	public AccUtilitiesRegenerateInputVatItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public AccUtilitiesRegenerateInputVatItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	@Override
	public String getMessage() {
		return message;
	}
	public AccUtilitiesRegenerateInputVatItem setMessage(String message) {
		this.message = message;
		return this;
	}
	public Integer getYear() {
		return year;
	}
	public AccUtilitiesRegenerateInputVatItem setYear(Integer year) {
		this.year = year;
		return this;
	}
	public boolean isRegenerable() {
		return regenerable;
	}
	public AccUtilitiesRegenerateInputVatItem setRegenerable(boolean regenerable) {
		this.regenerable = regenerable;
		return this;
	}
	
}
