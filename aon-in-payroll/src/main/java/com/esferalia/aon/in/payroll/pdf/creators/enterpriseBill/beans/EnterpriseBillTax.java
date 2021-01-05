package com.esferalia.aon.in.payroll.pdf.creators.enterpriseBill.beans;

public class EnterpriseBillTax {

	private double base;
	private double percentage;
	private String type;
	private double quota;

	public EnterpriseBillTax(double base, double percentage, String type, double quota) {
		this.base = base;
		this.percentage = percentage;
		this.type = type;
		this.quota = quota;
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

	@Override
	public String toString() {
		return "EnterpriseBillTax{" +
				"base=" + base +
				", percentage=" + percentage +
				", type='" + type + '\'' +
				", quota=" + quota +
				'}';
	}
}
