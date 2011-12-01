package com.code.aon.accounting.summary;

import com.code.aon.common.util.CommonUtil;

public class SummaryMonthly extends Summary {

	private Double[] months;

	public SummaryMonthly() {
		super();
	}

	public Double[] getMonths() {
		return months;
	}

	public void setMonths(Double[] months) {
		this.months = months;
	}

	public double getTotal() {
		double total = 0;
		if (months != null) {
			for (Double amount: months) {
				total = CommonUtil.round(total + amount);
			}
		}
		return total;
	}
}
