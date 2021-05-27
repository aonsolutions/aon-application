package com.code.aon.file.tax.model.MOD303.y2013;

@Deprecated
public class Breakdown {

	private double percent;
	private double taxableBase;
	private double quota;
	private double deductibleQuota;
	
	public Breakdown() {
		
	}
	public Breakdown(double percent,double taxableBase,double quota) {
		this.percent = percent;
		this.taxableBase = taxableBase;
		this.quota = quota;
	}
	public Breakdown(double percent,double taxableBase,double quota,double deductibleQuota) {
		this(percent, taxableBase, quota);
		this.deductibleQuota = deductibleQuota;
	}
	
	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}
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
