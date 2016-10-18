package com.code.aon.accounting.summary;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;

public class SummaryMonthly extends Summary {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Double[] months;

	public SummaryMonthly() {
		super();
		Double[] months = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
		setMonths(months);
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
	
	@Override
	public void add(Summary summary) {
		super.add(summary);
		if (summary instanceof SummaryMonthly) {
			SummaryMonthly sm = (SummaryMonthly) summary;
			for (int i = 0; i < months.length; i++) {
				getMonths()[i] = CommonUtil.round(getMonths()[i] + sm.getMonths()[i],2);
			}
			
			
		}
	}
	
	@Override
	public boolean isEmpty() {
		boolean empty = true;
		for (int i = 0; i < months.length; i++) {
			if (getMonths()[i] != 0) {
				empty = false;
			};
		}
		return super.isEmpty() && empty;
	}
}
