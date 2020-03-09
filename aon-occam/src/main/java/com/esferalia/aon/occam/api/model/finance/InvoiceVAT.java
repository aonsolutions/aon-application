package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.VatDeductionType;

public class InvoiceVAT implements Serializable {

	private static final long serialVersionUID = 8897444490096530091L;

	private boolean quotaEdited;
	private boolean surchargeQuotaEdited;
	private boolean deductibleQuotaEdited;
	
	private VatDeductionType vatDeductionType;
	private double base;
	private double percentage;
	private double quota;
	private double surcharge;
	private double surchargeQuota;
	private Integer investAsset;
	private double deductiblePercent;
	private double deductibleQuota;
	private boolean withholding;
	private boolean prepayment;
	
	private Integer outputAccountId;
	private String outputAccountCode;
	private String outputAccountDescription;
	private Integer inputAccountId;
	private String inputAccountCode;
	private String inputAccountDescription;
	private Integer adjAccountId;
	private String adjAccountCode;
	private String adjAccountDescription;
	
	private Integer expAccountId;
	private String expAccountCode;
	private String expAccountDescription;

	public boolean isAnyquotaEdited() {
		return isQuotaEdited() || isSurchargeQuotaEdited() || isDeductibleQuotaEdited();
	}
	public boolean isQuotaEdited() {
		return quotaEdited;
	}
	public InvoiceVAT setQuotaEdited(boolean quotaEdited) {
		this.quotaEdited = quotaEdited;
		return this;
	}
	public boolean isSurchargeQuotaEdited() {
		return surchargeQuotaEdited;
	}
	public InvoiceVAT setSurchargeQuotaEdited(boolean surchargeQuotaEdited) {
		this.surchargeQuotaEdited = surchargeQuotaEdited;
		return this;
	}
	public boolean isDeductibleQuotaEdited() {
		return deductibleQuotaEdited;
	}
	public InvoiceVAT setDeductibleQuotaEdited(boolean deductibleQuotaEdited) {
		this.deductibleQuotaEdited = deductibleQuotaEdited;
		return this;
	}
	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}

	public InvoiceVAT setVatDeductionType(VatDeductionType vatDeductionType) {
		this.vatDeductionType = vatDeductionType;
		return this;
	}

	public double getBase() {
		return base;
	}

	public InvoiceVAT setBase(double base) {
		this.base = base;
		return this;
	}

	public double getPercentage() {
		return percentage;
	}

	public InvoiceVAT setPercentage(double percentage) {
		this.percentage = percentage;
		return this;
	}

	public double getQuota() {
		return quota;
	}

	public InvoiceVAT setQuota(double quota) {
		this.quota = quota;
		return this;
	}

	public double getSurcharge() {
		return surcharge;
	}

	public InvoiceVAT setSurcharge(double surcharge) {
		this.surcharge = surcharge;
		return this;
	}

	public double getSurchargeQuota() {
		return surchargeQuota;
	}

	public InvoiceVAT setSurchargeQuota(double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
		return this;
	}

	public double getDeductibleQuota() {
		return deductibleQuota;
	}

	public InvoiceVAT setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
		return this;
	}
	
	public boolean isWithholding() {
		return withholding;
	}
	public InvoiceVAT setWithholding(boolean withholding) {
		this.withholding = withholding;
		return this;
	}
	public boolean isPrepayment() {
		return prepayment;
	}
	public InvoiceVAT setPrepayment(boolean prepayment) {
		this.prepayment = prepayment;
		return this;
	}
	
	public Integer getInvestAsset() {
		return investAsset;
	}
	
	public InvoiceVAT setInvestAsset(Integer investAsset) {
		this.investAsset = investAsset;
		return this;
	}
	
	public double getDeductiblePercent() {
		return deductiblePercent;
	}

	public InvoiceVAT setDeductiblePercent(double deductiblePercent) {
		this.deductiblePercent = deductiblePercent;
		return this;
	}

	public Integer getOutputAccountId() {
		return outputAccountId;
	}

	public InvoiceVAT setOutputAccountId(Integer outputAccountId) {
		this.outputAccountId = outputAccountId;
		return this;
	}

	public String getOutputAccountCode() {
		return outputAccountCode;
	}

	public InvoiceVAT setOutputAccountCode(String outputAccountCode) {
		this.outputAccountCode = outputAccountCode;
		return this;
	}

	public String getOutputAccountDescription() {
		return outputAccountDescription;
	}

	public InvoiceVAT setOutputAccountDescription(String outputAccountDescription) {
		this.outputAccountDescription = outputAccountDescription;
		return this;
	}

	public Integer getInputAccountId() {
		return inputAccountId;
	}

	public InvoiceVAT setInputAccountId(Integer inputAccountId) {
		this.inputAccountId = inputAccountId;
		return this;
	}

	public String getInputAccountCode() {
		return inputAccountCode;
	}

	public InvoiceVAT setInputAccountCode(String inputAccountCode) {
		this.inputAccountCode = inputAccountCode;
		return this;
	}

	public String getInputAccountDescription() {
		return inputAccountDescription;
	}

	public InvoiceVAT setInputAccountDescription(String inputAccountDescription) {
		this.inputAccountDescription = inputAccountDescription;
		return this;
	}

	public Integer getExpAccountId() {
		return expAccountId;
	}

	public InvoiceVAT setExpAccountId(Integer expAccountId) {
		this.expAccountId = expAccountId;
		return this;
	}

	public String getExpAccountCode() {
		return expAccountCode;
	}

	public InvoiceVAT setExpAccountCode(String expAccountCode) {
		this.expAccountCode = expAccountCode;
		return this;
	}

	public String getExpAccountDescription() {
		return expAccountDescription;
	}

	public InvoiceVAT setExpAccountDescription(String expAccountDescription) {
		this.expAccountDescription = expAccountDescription;
		return this;
	}

	public Integer getAdjAccountId() {
		return adjAccountId;
	}

	public InvoiceVAT setAdjAccountId(Integer adjAccountId) {
		this.adjAccountId = adjAccountId;
		return this;
	}

	public String getAdjAccountCode() {
		return adjAccountCode;
	}

	public InvoiceVAT setAdjAccountCode(String adjAccountCode) {
		this.adjAccountCode = adjAccountCode;
		return this;
	}

	public String getAdjAccountDescription() {
		return adjAccountDescription;
	}

	public InvoiceVAT setAdjAccountDescription(String adjAccountDescription) {
		this.adjAccountDescription = adjAccountDescription;
		return this;
	}
}
