package net.aonsolutions.vat.change;

import java.io.Serializable;

public class VatTaxDetail implements Serializable {

	private static final long serialVersionUID = 5920699631492262690L;
	
	private Integer id;
	private Integer vatTax;
	private int domain;
	private String key;
	private double percent;
	private double taxableBase;
	private double quota;
	private double deductibleQuota;
	private double taxableBaseAdjust;
	private double quotaAdjust;
	private double deductibleQuotaAdjust;
	private double taxableBaseAccumulated;
	private double quotaAccumulated;
	private double deductibleQuotaAccumulated;
	private double taxableBaseDeclared;
	private double quotaDeclared;
	private double deductibleQuotaDeclared;
	private double taxableBaseResult;
	private double quotaResult;
	private double deductibleQuotaResult;

	public Integer getId() {
		return id;
	}
	public VatTaxDetail setId(Integer id) {
		this.id = id;
		return this;
	}
	public Integer getVatTax() {
		return vatTax;
	}
	public VatTaxDetail setVatTax(Integer vatTax) {
		this.vatTax = vatTax;
		return this;
	}
	public int getDomain() {
		return domain;
	}
	public VatTaxDetail setDomain(int domain) {
		this.domain = domain;
		return this;
	}
	public String getKey() {
		return key;
	}
	public VatTaxDetail setKey(String key) {
		this.key = key;
		return this;
	}
	public double getPercent() {
		return percent;
	}
	public VatTaxDetail setPercent(double percent) {
		this.percent = percent;
		return this;
	}
	public double getTaxableBase() {
		return taxableBase;
	}
	public VatTaxDetail setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
		return this;
	}
	public double getQuota() {
		return quota;
	}
	public VatTaxDetail setQuota(double quota) {
		this.quota = quota;
		return this;
	}
	public double getDeductibleQuota() {
		return deductibleQuota;
	}
	public VatTaxDetail setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
		return this;
	}
	public double getTaxableBaseAdjust() {
		return taxableBaseAdjust;
	}
	public VatTaxDetail setTaxableBaseAdjust(double taxableBaseAdjust) {
		this.taxableBaseAdjust = taxableBaseAdjust;
		return this;
	}
	public double getQuotaAdjust() {
		return quotaAdjust;
	}
	public VatTaxDetail setQuotaAdjust(double quotaAdjust) {
		this.quotaAdjust = quotaAdjust;
		return this;
	}
	public double getDeductibleQuotaAdjust() {
		return deductibleQuotaAdjust;
	}
	public VatTaxDetail setDeductibleQuotaAdjust(double deductibleQuotaAdjust) {
		this.deductibleQuotaAdjust = deductibleQuotaAdjust;
		return this;
	}
	public double getTaxableBaseAccumulated() {
		return taxableBaseAccumulated;
	}
	public VatTaxDetail setTaxableBaseAccumulated(double taxableBaseAccumulated) {
		this.taxableBaseAccumulated = taxableBaseAccumulated;
		return this;
	}
	public double getQuotaAccumulated() {
		return quotaAccumulated;
	}
	public VatTaxDetail setQuotaAccumulated(double quotaAccumulated) {
		this.quotaAccumulated = quotaAccumulated;
		return this;
	}
	public double getDeductibleQuotaAccumulated() {
		return deductibleQuotaAccumulated;
	}
	public VatTaxDetail setDeductibleQuotaAccumulated(double deductibleQuotaAccumulated) {
		this.deductibleQuotaAccumulated = deductibleQuotaAccumulated;
		return this;
	}
	public double getTaxableBaseDeclared() {
		return taxableBaseDeclared;
	}
	public VatTaxDetail setTaxableBaseDeclared(double taxableBaseDeclared) {
		this.taxableBaseDeclared = taxableBaseDeclared;
		return this;
	}
	public double getQuotaDeclared() {
		return quotaDeclared;
	}
	public VatTaxDetail setQuotaDeclared(double quotaDeclared) {
		this.quotaDeclared = quotaDeclared;
		return this;
	}
	public double getDeductibleQuotaDeclared() {
		return deductibleQuotaDeclared;
	}
	public VatTaxDetail setDeductibleQuotaDeclared(double deductibleQuotaDeclared) {
		this.deductibleQuotaDeclared = deductibleQuotaDeclared;
		return this;
	}
	public double getTaxableBaseResult() {
		return taxableBaseResult;
	}
	public VatTaxDetail setTaxableBaseResult(double taxableBaseResult) {
		this.taxableBaseResult = taxableBaseResult;
		return this;
	}
	public double getQuotaResult() {
		return quotaResult;
	}
	public VatTaxDetail setQuotaResult(double quotaResult) {
		this.quotaResult = quotaResult;
		return this;
	}
	public double getDeductibleQuotaResult() {
		return deductibleQuotaResult;
	}
	public VatTaxDetail setDeductibleQuotaResult(double deductibleQuotaResult) {
		this.deductibleQuotaResult = deductibleQuotaResult;
		return this;
	}

}
