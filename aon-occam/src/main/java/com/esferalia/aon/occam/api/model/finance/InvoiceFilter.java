package com.esferalia.aon.occam.api.model.finance;

import java.util.Date;

public class InvoiceFilter {

	private int domain;
	
	private boolean salesEnabled;
	private boolean purchasesEnabled;
	private boolean expensesEnabled;
	private boolean undeductibleExpensesEnabled;

	private Date fromDate;
	private Date toDate;
	
	public int getDomain() {
		return domain;
	}
	public void setDomain(int domain) {
		this.domain = domain;
	}
	public boolean isSalesEnabled() {
		return salesEnabled;
	}
	public void setSalesEnabled(boolean salesEnabled) {
		this.salesEnabled = salesEnabled;
	}
	public boolean isPurchasesEnabled() {
		return purchasesEnabled;
	}
	public void setPurchasesEnabled(boolean purchasesEnabled) {
		this.purchasesEnabled = purchasesEnabled;
	}
	public boolean isExpensesEnabled() {
		return expensesEnabled;
	}
	public void setExpensesEnabled(boolean expensesEnabled) {
		this.expensesEnabled = expensesEnabled;
	}
	public boolean isUndeductibleExpensesEnabled() {
		return undeductibleExpensesEnabled;
	}
	public void setUndeductibleExpensesEnabled(boolean undeductibleExpensesEnabled) {
		this.undeductibleExpensesEnabled = undeductibleExpensesEnabled;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}
	
	

}
