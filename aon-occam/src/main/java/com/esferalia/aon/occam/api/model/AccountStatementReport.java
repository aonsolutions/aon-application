package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;

public class AccountStatementReport implements Serializable {
	
	private static final long serialVersionUID = -3255950085921957296L;
	
	private Account account;
	private Date from;
	private Date to;
	
	private LinkedList<AccountStatement> summary;
	private LinkedList<AccountStatement> details;
	
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	public Date getFrom() {
		return from;
	}
	public void setFrom(Date from) {
		this.from = from;
	}
	public Date getTo() {
		return to;
	}
	public void setTo(Date to) {
		this.to = to;
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
