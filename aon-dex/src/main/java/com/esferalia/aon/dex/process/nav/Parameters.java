package com.esferalia.aon.dex.process.nav;

import java.util.Date;

public class Parameters {

	private String wsUrl;
	private String userName;
	private String password;
	private int domainId;
	private String domainName;
	private Date fromDate;
	private Date toDate;
	private int invoiceId;
	private String invoiceReferenceCode;
	private int fbatchId;
	private String fbatchDescription;

	public String getWsUrl() {
		return wsUrl;
	}
	public void setWsUrl(String wsUrl) {
		this.wsUrl = wsUrl;
	}

	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public int getDomainId() {
		return domainId;
	}
	public void setDomainId(int domainId) {
		this.domainId = domainId;
	}

	public String getDomainName() {
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
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

	public int getInvoiceId() {
		return invoiceId;
	}
	public void setInvoiceId(int invoiceId) {
		this.invoiceId = invoiceId;
	}

	public String getInvoiceReferenceCode() {
		return invoiceReferenceCode;
	}
	public void setInvoiceReferenceCode(String invoiceReferenceCode) {
		this.invoiceReferenceCode = invoiceReferenceCode;
	}

	public int getFbatchId() {
		return fbatchId;
	}
	public void setFbatchId(int fbatchId) {
		this.fbatchId = fbatchId;
	}

	public String getFbatchDescription() {
		return fbatchDescription;
	}
	public void setFbatchDescription(String fbatchDescription) {
		this.fbatchDescription = fbatchDescription;
	}

}