package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

public class AccountStatementReport implements Serializable {
	
	private static final long serialVersionUID = -3255950085921957296L;
	
	private EnterpriseActivity selectedActivity;
	private AccountingReportParams params;
	
	private LinkedList<AccountStatement> summary;
	private LinkedList<AccountStatement> details;
	
	public AccountingReportParams  getParams() {
		return params;
	}
	public AccountStatementReport setParams(AccountingReportParams params) {
		this.params = params;
		return this; 
	}
	
	public EnterpriseActivity getSelectedActivity() {
		return selectedActivity;
	}
	public AccountStatementReport setSelectedActivity(EnterpriseActivity selectedActivity) {
		this.selectedActivity = selectedActivity;
		return this;
	}
	
	public Account getAccount() {
		return params != null?params.getAccount():null;
	}
	public Date getFrom() {
		return params != null?params.getFromDate():null;
	}
	public Date getTo() {
		return params != null?params.getToDate():null;
	}
	public LinkedList<AccountStatement> getSummary() {
		return summary;
	}
	public void setSummary(LinkedList<AccountStatement> summary) {
		this.summary = summary;
	}
	public LinkedList<AccountStatement> getDetails() {
		return details;
	}
	public void setDetails(LinkedList<AccountStatement> details) {
		this.details = details;
	}
	
}
