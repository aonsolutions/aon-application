package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.TaxType;
import com.esferalia.aon.occam.api.model.type.VatDeductionType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class InvoiceTax implements Serializable {

	private static final long serialVersionUID = 7037774854336091259L;

	private Integer id;
	private Integer domain;
	private TaxType taxType;
	private double base;
	private double percentage;
	private double quota;
	private double surcharge;
	private double surchargeQuota;
	private VatDeductionType vatDeductionType;
	private Integer investAsset;
	private double deductiblePercent;
	private double deductibleQuota;
	private boolean withholding;
	private WithholdingType withholdingType;
	
	private Integer account;
	
	public Integer getId() {
		return id;
	}
	public InvoiceTax setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getDomain() {
		return domain;
	}
	public InvoiceTax setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	public TaxType getTaxType() {
		return taxType;
	}
	public InvoiceTax setTaxType(TaxType taxType) {
		this.taxType = taxType;
		return this;
	}
	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}

	public InvoiceTax setVatDeductionType(VatDeductionType vatDeductionType) {
		this.vatDeductionType = vatDeductionType;
		return this;
	}

	public double getBase() {
		return base;
	}

	public InvoiceTax setBase(double base) {
		this.base = base;
		return this;
	}

	public double getPercentage() {
		return percentage;
	}

	public InvoiceTax setPercentage(double percentage) {
		this.percentage = percentage;
		return this;
	}

	public double getQuota() {
		return quota;
	}

	public InvoiceTax setQuota(double quota) {
		this.quota = quota;
		return this;
	}

	public double getSurcharge() {
		return surcharge;
	}

	public InvoiceTax setSurcharge(double surcharge) {
		this.surcharge = surcharge;
		return this;
	}

	public double getSurchargeQuota() {
		return surchargeQuota;
	}

	public InvoiceTax setSurchargeQuota(double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
		return this;
	}

	public double getDeductibleQuota() {
		return deductibleQuota;
	}

	public InvoiceTax setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
		return this;
	}
	
	public boolean isWithholding() {
		return withholding;
	}
	public InvoiceTax setWithholding(boolean withholding) {
		this.withholding = withholding;
		return this;
	}
	
	public Integer getInvestAsset() {
		return investAsset;
	}
	
	public InvoiceTax setInvestAsset(Integer investAsset) {
		this.investAsset = investAsset;
		return this;
	}
	
	public double getDeductiblePercent() {
		return deductiblePercent;
	}

	public InvoiceTax setDeductiblePercent(double deductiblePercent) {
		this.deductiblePercent = deductiblePercent;
		return this;
	}
	
	public WithholdingType getWithholdingType() {
		return withholdingType;
	}
	public InvoiceTax setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
		return this;
	}
	
	public Integer getAccount() {
		return account;
	}
	public InvoiceTax setAccount(Integer account) {
		this.account = account;
		return this;
	}
}
