package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.Domain;

public class AmortizationType implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Domain domain;
	private String description;
	private String fixedAssetAccount;
	private String accumulatedAccount;
	private String allocationAccount;
	private Double percentage;

	private boolean modify = false;

	public AmortizationType() {
		super();
	}

	public Integer getId() {
		return id;
	}

	public AmortizationType setId(Integer id) {
		this.id = id;
		return this;
	}

	public Domain getDomain() {
		return domain;
	}

	public AmortizationType setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public AmortizationType setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getFixedAssetAccount() {
		return fixedAssetAccount;
	}

	public AmortizationType setFixedAssetAccount(String fixedAssetAccount) {
		this.fixedAssetAccount = fixedAssetAccount;
		return this;
	}

	public String getAccumulatedAccount() {
		return accumulatedAccount;
	}

	public AmortizationType setAccumulatedAccount(String accumulatedAccount) {
		this.accumulatedAccount = accumulatedAccount;
		return this;
	}

	public String getAllocationAccount() {
		return allocationAccount;
	}

	public AmortizationType setAllocationAccount(String allocationAccount) {
		this.allocationAccount = allocationAccount;
		return this;
	}

	public Double getPercentage() {
		return percentage;
	}

	public AmortizationType setPercentage(Double percentage) {
		this.percentage = percentage;
		return this;
	}

	public boolean isModify() {
		return modify;
	}

	public AmortizationType setModify(boolean modify) {
		this.modify = modify;
		return this;
	}
	
}
