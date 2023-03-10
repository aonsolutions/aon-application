package com.esferalia.aon.gwt.fiscal.client.accounting.js;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;

public class JsAccountingBreakdown extends JavaScriptObject {
	
	protected JsAccountingBreakdown() {
	}
	
	public final native int getEntryId() /*-{
		return this.entryId;
	}-*/;
	public final native int getJournal() /*-{
		return this.journal;
	}-*/;
	public final native String getIssueDateString() /*-{
		return this.issueDate;
	}-*/;
	public final Date getIssueDate() {
		return getIssueDateString() == null ? null : new Date( Long.valueOf(getIssueDateString()));
	}
	public final native int getActivity() /*-{
		return this.activity;
	}-*/;
	public final native String getActivityDescription() /*-{
		return this.activityDescription;
	}-*/;
	public final native String getEpigraphSection() /*-{
		return this.epigraphSection;
	}-*/;
	public final native String getEpigraph() /*-{
		return this.epigraph;
	}-*/;
	public final native int getRegime() /*-{
		return this.regime;
	}-*/;
	public final native int getAccount() /*-{
		return this.account;
	}-*/;
	public final native String getAccountCode() /*-{
		return this.accountCode;
	}-*/;
		public final native String getAccountDescription() /*-{
		return this.accountDescription;
	}-*/;
		public final native String getConcept() /*-{
		return this.concept;
	}-*/;
	public final native double getDebit()/*-{
		return this.debit;
	}-*/;
	public final native double getCredit()/*-{
		return this.credit;
	}-*/;
	
}
