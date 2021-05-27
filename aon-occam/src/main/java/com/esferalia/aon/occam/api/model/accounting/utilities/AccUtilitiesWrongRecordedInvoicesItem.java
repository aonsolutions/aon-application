package com.esferalia.aon.occam.api.model.accounting.utilities;

import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountEntry;
import com.esferalia.aon.occam.api.model.finance.Invoice;

public class AccUtilitiesWrongRecordedInvoicesItem implements IAccUtilitiesItem {
	
	private static final long serialVersionUID = -8812702540651915368L;
	private Integer domain;
	private String domainName;
	
	private Invoice invoice;
	private boolean onlyMarked;
	private LinkedList<AccountEntry> entries;
	private String message;
	
	@Override
	public AccUtilitiesItemType getType() {
		return AccUtilitiesItemType.WRONG_RECORDED_INVOICE;
	}
	@Override
	public Integer getDomain() {
		return domain;
	}
	public AccUtilitiesWrongRecordedInvoicesItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public AccUtilitiesWrongRecordedInvoicesItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}

	@Override
	public String getMessage() {
		return message;
	}
	public AccUtilitiesWrongRecordedInvoicesItem setMessage(String message) {
		this.message = message;
		return this;
	}
	public Invoice getInvoice() {
		return invoice;
	}
	public AccUtilitiesWrongRecordedInvoicesItem setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
	public LinkedList<AccountEntry> getEntries() {
		return entries;
	}
	public AccUtilitiesWrongRecordedInvoicesItem setEntries(LinkedList<AccountEntry> entries) {
		this.entries = entries;
		return this;
	}
	
	public int getCount() {
		return entries == null? 0 : entries.size();
	}
	
	public boolean isOnlyMarked() {
		return onlyMarked;
	}
	public AccUtilitiesWrongRecordedInvoicesItem setOnlyMarked(boolean onlyMarked) {
		this.onlyMarked = onlyMarked;
		return this;
	}
}
