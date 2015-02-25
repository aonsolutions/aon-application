package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.VatDeductionType;

public class InvoiceVAT implements Serializable {

	private static final long serialVersionUID = 8897444490096530091L;

	private VatDeductionType vatDeductionType;
	private double base;
	private double percentage;
	private double quota;
	private double surcharge;
	private double surchargeQuota;
	private double deductibleQuota;

	public VatDeductionType getVatDeductionType() {
		return vatDeductionType;
	}

	public void setVatDeductionType(VatDeductionType vatDeductionType) {
		this.vatDeductionType = vatDeductionType;
	}

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

	public double getSurcharge() {
		return surcharge;
	}

	public void setSurcharge(double surcharge) {
		this.surcharge = surcharge;
	}

	public double getSurchargeQuota() {
		return surchargeQuota;
	}

	public void setSurchargeQuota(double surchargeQuota) {
		this.surchargeQuota = surchargeQuota;
	}

	public double getDeductibleQuota() {
		return deductibleQuota;
	}

	public void setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
	}

}
