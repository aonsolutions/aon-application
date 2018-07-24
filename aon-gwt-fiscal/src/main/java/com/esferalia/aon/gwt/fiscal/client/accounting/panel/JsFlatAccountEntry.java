package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;

public class JsFlatAccountEntry extends JavaScriptObject {
	
	private static DateTimeFormat DATE_FORMAT = null;
	private static DateTimeFormat DATE_TIME_FORMAT = null;

	protected JsFlatAccountEntry() {
	}
	
	private static DateTimeFormat getDateFormat() {
		if (DATE_FORMAT == null)
			DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
		return DATE_FORMAT;
	}
	
	
	public final native int getEntryId() /*-{
		return this.entryId;
	}-*/;

	public final native int getEntryDomain() /*-{
		return this.entryDomain;
	}-*/;
	public final native int getEntryPeriod() /*-{
		return this.entryPeriod;
	}-*/;
	public final native String getEntryPeriodName() /*-{
		return this.entryPeriodName;
	}-*/;
	public final native String getEntryDateString() /*-{
		return this.entryDate;
	}-*/;
	public final Date getEntryDate() {
		return getDateFormat().parse(getEntryDateString());
	}
	public final native int getEntryType() /*-{
		return this.entryType;
	}-*/;
	public final native int getActivity() /*-{
		return this.activity;
	}-*/;
	public final native String getActivityName() /*-{
		return this.activityName;
	}-*/;
	public final native Integer getJournal() /*-{
		return this.journal;
	}-*/;
	public final native Integer getSecurityLevel() /*-{
		return this.securityLevel;
	}-*/;
	public final native String getComments() /*-{
		return this.comments;
	}-*/;
	public final native int getDetailId() /*-{
		return this.detailId;
	}-*/;
	public final native Integer getAccount() /*-{
		return this.account;
	}-*/;
	public final native String getAccountCode() /*-{
		return this.accountCode;
	}-*/;
	public final native String getAccountDescription() /*-{
		return this.accountDescription;
	}-*/;
	public final native Integer getLine() /*-{
		return this.line;
	}-*/;
	public final native String getConcept() /*-{
		return this.concept;
	}-*/;
	public final native Double getDebit() /*-{
		return this.debit;
	}-*/;
	public final native Double getCredit() /*-{
		return this.credit;
	}-*/;
	public final native Integer getBalancingAccount() /*-{
		return this.balancingAccount;
	}-*/;
	public final native String getBalancingAccountCode() /*-{
		return this.balancingAccountCode;
	}-*/;
	public final native String getBalancingAccountDescription() /*-{
		return this.balancingAccountDescription;
	}-*/;
	public final native String getDocumentNumber() /*-{
		return this.documentNumber;
	}-*/;
}
