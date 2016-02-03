package com.code.aon.accounting.report;

import java.io.Serializable;

import com.code.aon.AonVersion;

public class OperationReportTax implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String taxType;
	private Double percentage;
	private Double base;
	private Double quota;
	private Double surchargePercentage;
	private Double surchargeQuota;
	
	public String getTaxType() {
		return taxType;
	}
	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}
	public Double getPercentage() {
		return percentage;
	}
	public void setPercentage(Double percentage) {
		this.percentage = percentage;
	}
	public Double getBase() {
		return base;
	}
	public void setBase(Double base) {
		this.base = base;
	}
	public Double getQuota() {
		return quota;
	}
	public void setQuota(Double quota) {
		this.quota = quota;
	}
	public Double getSurchargePercentage() {
		return surchargePercentage;
	}
	public void setSurchargePercentage(Double surchargePercentage) {
		this.surchargePercentage = surchargePercentage;
	}
	public Double getSurchargeQuota() {
		return surchargeQuota;
	}
	public void setSurchargeQuota(Double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
	}
}
