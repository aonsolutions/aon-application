package com.code.aon.accounting.summary;

import java.util.List;

public class SummaryMonthly extends Summary {

	private List<Double> months;

	public SummaryMonthly() {
		super();
	}

	public SummaryMonthly(List<Double> months, String id, String description, double debit,
			double credit) {
		super(id, description, debit, credit);
		setMonths(months);
	}

	public List<Double> getMonths() {
		return months;
	}

	public void setMonths(List<Double> months) {
		this.months = months;
	}

	public double getTotal() {
		double total = 0;
		if (months != null) {
			for (Double amount: months) {
				total = round(total + amount);
			}
		}
		return total;
	}
}
