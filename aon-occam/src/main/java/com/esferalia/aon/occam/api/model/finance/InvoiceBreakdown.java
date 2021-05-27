package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.TaxType;

public class InvoiceBreakdown implements Serializable {

	private static final long serialVersionUID = -6917677513733867353L;
	
	private TaxType taxType;
	private double base;
	private double percentage;
	private double quota;
	private double surcharge;
	private double surchargeQuota;
	
	public TaxType getTaxType() {
		return taxType;
	}
	public InvoiceBreakdown setTaxType(TaxType taxType) {
		this.taxType = taxType;
		return this;
	}
	
	public double getBase() {
		return base;
	}
	public InvoiceBreakdown setBase(double base) {
		this.base = base;
		return this;
	}

	public double getPercentage() {
		return percentage;
	}
	public InvoiceBreakdown setPercentage(double percentage) {
		this.percentage = percentage;
		return this;
	}

	public double getQuota() {
		return quota;
	}
	public InvoiceBreakdown setQuota(double quota) {
		this.quota = quota;
		return this;
	}

	public double getSurcharge() {
		return surcharge;
	}
	public InvoiceBreakdown setSurcharge(double surcharge) {
		this.surcharge = surcharge;
		return this;
	}

	public double getSurchargeQuota() {
		return surchargeQuota;
	}
	public InvoiceBreakdown setSurchargeQuota(double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
		return this;
	}

}
