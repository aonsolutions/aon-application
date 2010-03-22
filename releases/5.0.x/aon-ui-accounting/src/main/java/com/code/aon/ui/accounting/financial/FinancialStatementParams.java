package com.code.aon.ui.accounting.financial;

import java.util.Date;

public class FinancialStatementParams {
	
	private Date financialDate;
	private Date excludeFinanceDate;
	private Date includeExpensesDate;
	
	public Date getFinancialDate() {
		return financialDate;
	}
	public void setFinancialDate(Date financialDate) {
		this.financialDate = financialDate;
	}
	public Date getExcludeFinanceDate() {
		return excludeFinanceDate;
	}
	public void setExcludeFinanceDate(Date excludeFinanceDate) {
		this.excludeFinanceDate = excludeFinanceDate;
	}
	public Date getIncludeExpensesDate() {
		return includeExpensesDate;
	}
	public void setIncludeExpensesDate(Date includeExpensesDate) {
		this.includeExpensesDate = includeExpensesDate;
	}
}
