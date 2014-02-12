package com.code.aon.accounting.util;

import java.util.Date;

public class AccountingFinanceCheckerParams {

	private int domain;
	private Date deadline;
	private boolean creditorsEnabled;
	private boolean suppliersEnabled;
	private boolean customersEnabled;
	private String accountCode;
	
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public Date getDeadline() {
		return deadline;
	}
	public void setDeadline(Date deadline) {
		this.deadline = deadline;
	}
	public boolean isCreditorsEnabled() {
		return creditorsEnabled;
	}
	public void setCreditorsEnabled(boolean creditorsEnabled) {
		this.creditorsEnabled = creditorsEnabled;
	}
	public boolean isSuppliersEnabled() {
		return suppliersEnabled;
	}
	public void setSuppliersEnabled(boolean suppliersEnabled) {
		this.suppliersEnabled = suppliersEnabled;
	}
	public boolean isCustomersEnabled() {
		return customersEnabled;
	}
	public void setCustomersEnabled(boolean customersEnabled) {
		this.customersEnabled = customersEnabled;
	}
	public String getAccountCode() {
		return accountCode;
	}
	public void setAccountCode(String accountCode) {
		this.accountCode = accountCode;
	}
}
