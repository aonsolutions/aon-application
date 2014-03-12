package com.code.aon.ui.fiscal.vat;

import java.io.Serializable;

import com.code.aon.common.AonVersion;

public class VatBreakdown implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private double base;
	private double percent;
	private double quota;
	
	public double getBase() {
		return base;
	}
	public void setBase(double base) {
		this.base = base;
	}
	public double getPercent() {
		return percent;
	}
	public void setPercent(double percent) {
		this.percent = percent;
	}

	public double getQuota() {
		return quota;
	}
	public void setQuota(double quota) {
		this.quota = quota;
	}
	
	
}
