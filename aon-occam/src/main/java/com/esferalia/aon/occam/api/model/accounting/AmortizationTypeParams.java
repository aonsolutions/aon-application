package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;

public class AmortizationTypeParams implements Serializable {

	private static final long serialVersionUID = -7078789958353911216L;
	
	private Integer domain;
	
	private String description;
	
	private String fixedAssetAccount;
	private String accumulatedAccount;
	private String allocationAccount;

	private Integer limit;
	private Integer offset;
	
	public AmortizationTypeParams() {
		super();
	}
	
	public Integer getDomain() {
		return domain;
	}
	
	public AmortizationTypeParams setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public String getDescription() {
		return description;
	}

	public AmortizationTypeParams setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getFixedAssetAccount() {
		return fixedAssetAccount;
	}

	public AmortizationTypeParams setFixedAssetAccount(String fixedAssetAccount) {
		this.fixedAssetAccount = fixedAssetAccount;
		return this;
	}

	public String getAccumulatedAccount() {
		return accumulatedAccount;
	}

	public AmortizationTypeParams setAccumulatedAccount(String accumulatedAccount) {
		this.accumulatedAccount = accumulatedAccount;
		return this;
	}

	public String getAllocationAccount() {
		return allocationAccount;
	}

	public AmortizationTypeParams setAllocationAccount(String allocationAccount) {
		this.allocationAccount = allocationAccount;
		return this;
	}

	public Integer getLimit() {
		return limit;
	}
	
	public AmortizationTypeParams setLimit(Integer limit) {
		this.limit = limit;
		return this;
	}
	
	public Integer getOffset() {
		return offset;
	}
	
	public AmortizationTypeParams setOffset(Integer offset) {
		this.offset = offset;
		return this;
	}
}
