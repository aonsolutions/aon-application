package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class InvoiceWithholding implements Serializable {

	private static final long serialVersionUID = 8897444490096530091L;

	private WithholdingType withholdingType;
	private double base;
	private double percentage;
	private double quota;
	private Integer accountId;
	private String accountCode;
	private String accountDescription;
	
	private boolean quotaEdited;

	public double getBase() {
		return base;
	}

	public InvoiceWithholding setBase(double base) {
		this.base = base;
		return this;
	}

	public double getPercentage() {
		return percentage;
	}

	public InvoiceWithholding setPercentage(double percentage) {
		this.percentage = percentage;
		return this;
	}

	public double getQuota() {
		return quota;
	}

	public InvoiceWithholding setQuota(double quota) {
		this.quota = quota;
		return this;
	}

	public WithholdingType getWithholdingType() {
		return withholdingType;
	}

	public InvoiceWithholding setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
		return this;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public InvoiceWithholding setAccountId(Integer accountId) {
		this.accountId = accountId;
		return this;
	}

	public String getAccountCode() {
		return accountCode;
	}

	public InvoiceWithholding setAccountCode(String accountCode) {
		this.accountCode = accountCode;
		return this;
	}

	public String getAccountDescription() {
		return accountDescription;
	}

	public InvoiceWithholding setAccountDescription(String accountDescription) {
		this.accountDescription = accountDescription;
		return this;
	}
	
	public boolean isQuotaEdited() {
		return quotaEdited;
	}
	public InvoiceWithholding setQuotaEdited(boolean quotaEdited) {
		this.quotaEdited = quotaEdited;
		return this;
	}
	
}
