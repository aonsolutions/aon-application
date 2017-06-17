package com.esferalia.aon.occam.api.model.fiscal;

public class VatData {
	private double base;
	private double percentage;
	private double quota;

	private double surchargePercent;
	private double surchargeQuota;
	public double getBase() {
		return base;
	}
	public VatData setBase(double base) {
		this.base = base;
		return this;
	}
	public double getPercentage() {
		return percentage;
	}
	public VatData setPercentage(double percentage) {
		this.percentage = percentage;
		return this;
	}
	public double getQuota() {
		return quota;
	}
	public VatData setQuota(double quota) {
		this.quota = quota;
		return this;
	}
	public double getSurchargePercent() {
		return surchargePercent;
	}
	public VatData setSurchargePercent(double surchargePercent) {
		this.surchargePercent = surchargePercent;
		return this;
	}
	public double getSurchargeQuota() {
		return surchargeQuota;
	}
	public VatData setSurchargeQuota(double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
		return this;
	}
	
	
}
