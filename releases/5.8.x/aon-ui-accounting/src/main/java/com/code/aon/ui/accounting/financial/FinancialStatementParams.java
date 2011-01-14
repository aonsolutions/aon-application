package com.code.aon.ui.accounting.financial;

import java.util.Date;

import com.code.aon.common.enumeration.SecurityLevel;

public class FinancialStatementParams {
	
	private Date financialDate;
	private Date excludeFinanceDate;
	private Date includeExpensesDate;
	private SecurityLevel securityLevel;
	
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
	public SecurityLevel getSecurityLevel() {
		return securityLevel;
	}
	public void setSecurityLevel(SecurityLevel securityLevel) {
		this.securityLevel = securityLevel;
	}
}
