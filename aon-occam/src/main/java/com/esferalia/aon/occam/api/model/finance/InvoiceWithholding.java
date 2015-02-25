package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.WithholdingType;

public class InvoiceWithholding implements Serializable {

	private static final long serialVersionUID = 8897444490096530091L;

	private WithholdingType withholdingType;
	
	private double base;
	private double percentage;
	private double quota;

	public double getBase() {
		return base;
	}

	public void setBase(double base) {
		this.base = base;
	}

	public double getPercentage() {
		return percentage;
	}

	public void setPercentage(double percentage) {
		this.percentage = percentage;
	}

	public double getQuota() {
		return quota;
	}

	public void setQuota(double quota) {
		this.quota = quota;
	}

	public WithholdingType getWithholdingType() {
		return withholdingType;
	}

	public void setWithholdingType(WithholdingType withholdingType) {
		this.withholdingType = withholdingType;
	}

}
