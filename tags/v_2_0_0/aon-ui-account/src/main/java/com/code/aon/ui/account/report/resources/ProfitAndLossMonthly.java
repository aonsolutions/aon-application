package com.code.aon.ui.account.report.resources;

public class ProfitAndLossMonthly extends ProfitAndLossSummary {

	private Integer month;

	public ProfitAndLossMonthly(String account, String description, Integer month, double debit, double credit) {
	    super(account, description, debit, credit);
        this.month = month;
	}

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

}
