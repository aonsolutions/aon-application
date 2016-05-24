package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.VatDeductionType;

public class InvoiceVAT implements Serializable {

	private static final long serialVersionUID = 8897444490096530091L;

	private VatDeductionType vatDeductionType;
	private double base;
	private double percentage;
	private double quota;
	private double surcharge;
	private double surchargeQuota;
	private double deductibleQuota;
	private Integer outputAccountId;
	private String outputAccountCode;
	private String outputAccountDescription;
	private Integer inputAccountId;
	private String inputAccountCode;
	private String inputAccountDescription;
	
	private Integer expAccountId;
	private String expAccountCode;
	private String expAccountDescription;

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
}
