package com.esferalia.aon.in.payroll.pdf.maker.invoice.bean;

public class InvoiceTax {

	private double base;
	private double percentage;
	private String type;
	private double quota;

	public InvoiceTax(double base, double percentage, String type, double quota) {
		this.base		= base;
		this.percentage	= percentage;
		this.type		= type;
		this.quota		= quota;
	}

	public double getBase() {
		return base;
	}

	public double getPercentage() {
		return percentage;
	}

	public String getType() {
		return type;
	}

	public double getQuota() {
		return quota;
	}
}
