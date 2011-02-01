package com.code.aon.fiscal.vat.tax;



public class VatTaxAmount {
	private double taxableBase;
	private double quota;
	private double deductibleQuota;

	public double getTaxableBase() {
		return taxableBase;
	}
	public void setTaxableBase(double taxableBase) {
		this.taxableBase = taxableBase;
	}
	
	public double getQuota() {
		return quota;
	}
	public void setQuota(double quota) {
		this.quota = quota;
	}
	
	public double getDeductibleQuota() {
		return deductibleQuota;
	}
	public void setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
	}
}
