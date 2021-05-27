package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class VatSummaryContext implements Serializable {
	
	private static final long serialVersionUID = 2683060057390169937L;
	
	private boolean output;
	private VatSummaryType summaryType;
	private double base;
	private double percentage;
	private double quota;
	private double deductibleQuota;
	
	public boolean isOutput() {
		return output;
	}
	public VatSummaryContext setOutput(boolean output) {
		this.output = output;
		return this;
	}
	public VatSummaryType getSummaryType() {
		return summaryType;
	}
	public VatSummaryContext setSummaryType(VatSummaryType summaryType) {
		this.summaryType = summaryType;
		return this;
	}
	public double getBase() {
		return base;
	}
	public VatSummaryContext setBase(double base) {
		this.base = base;
		return this;
	}
	public double getPercentage() {
		return percentage;
	}
	public VatSummaryContext setPercentage(double percentage) {
		this.percentage = percentage;
		return this;
	}
	public double getQuota() {
		return quota;
	}
	public VatSummaryContext setQuota(double quota) {
		this.quota = quota;
		return this;
	}
	public double getDeductibleQuota() {
		return deductibleQuota;
	}
	public VatSummaryContext setDeductibleQuota(double deductibleQuota) {
		this.deductibleQuota = deductibleQuota;
		return this;
	}
	
}
