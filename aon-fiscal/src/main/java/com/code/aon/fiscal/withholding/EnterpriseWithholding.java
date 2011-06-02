package com.code.aon.fiscal.withholding;

public class EnterpriseWithholding {
	
	private int enterpriseId;
	private String enterprise;
	private int count;
	private double taxableBase;
	private double quota;

	private int workCount;
	private double workTaxableBase;
	private double workQuota;
	
	public int getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(int enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	public String getEnterprise() {
		return enterprise;
	}
	public void setEnterprise(String enterprise) {
		this.enterprise = enterprise;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
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
	public int getWorkCount() {
		return workCount;
	}
	public void setWorkCount(int workCount) {
		this.workCount = workCount;
	}
	public double getWorkTaxableBase() {
		return workTaxableBase;
	}
	public void setWorkTaxableBase(double workTaxableBase) {
		this.workTaxableBase = workTaxableBase;
	}
	public double getWorkQuota() {
		return workQuota;
	}
	public void setWorkQuota(double workQuota) {
		this.workQuota = workQuota;
	}

	
	
}
