package com.esferalia.aon.occam.api.model.finance.utilities;

import com.esferalia.aon.occam.api.model.finance.Finance;
import com.esferalia.aon.occam.api.model.finance.FinanceTracking;

public class FinanceInvoiceIntegrityItem implements IFinanceUtilitiesItem {
	
	private static final long serialVersionUID = 4569800197470285266L;
	
	private Integer domain;
	private String domainName;
	
	private Finance finance;
	private FinanceTracking tracking;
	private String message;
	
	@Override
	public Integer getDomain() {
		return domain;
	}
	public FinanceInvoiceIntegrityItem setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	@Override
	public String getDomainName() {
		return domainName;
	}
	public FinanceInvoiceIntegrityItem setDomainName(String domainName) {
		this.domainName = domainName;
		return this;
	}
	@Override
	public FinanceUtilitiesItemType getType() {
		return FinanceUtilitiesItemType.FINANCE_INVOICE_INTEGRITY_CHECK;
	}
	@Override
	public String getMessage() {
		return message;
	}
	public FinanceInvoiceIntegrityItem setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public Finance getFinance() {
		return finance;
	}
	public FinanceInvoiceIntegrityItem setFinance(Finance finance) {
		this.finance = finance;
		return this;
	}
	
	public FinanceTracking getTracking() {
		return tracking;
	}
	public FinanceInvoiceIntegrityItem setTracking(FinanceTracking tracking) {
		this.tracking = tracking;
		return this;
	}
}
