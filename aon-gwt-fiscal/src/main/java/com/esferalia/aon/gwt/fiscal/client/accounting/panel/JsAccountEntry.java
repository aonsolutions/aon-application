package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import java.util.Date;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;

public class JsAccountEntry extends JavaScriptObject {
	
	private static DateTimeFormat DATE_FORMAT = null;
	private static DateTimeFormat DATE_TIME_FORMAT = null;

	protected JsAccountEntry() {
	}
	
	private static DateTimeFormat getDateFormat() {
		if (DATE_FORMAT == null)
			DATE_FORMAT = DateTimeFormat.getFormat("yyyy-MM-dd");
		return DATE_FORMAT;
	}
	
	public final native int getDomain() /*-{
		return this.domain;
	}-*/;
	public final native int getId() /*-{
		return this.id;
	}-*/;
	public final native int getPeriod() /*-{
		return this.period;
	}-*/;
	public final native String getPeriodName() /*-{
		return this.periodName;
	}-*/;
	public final native int getPeriodStatus() /*-{
		return this.periodStatus;
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
	public final native Integer getActivity() /*-{
		return this.activity;
	}-*/;
	public final native String getActivityDescription() /*-{
		return this.activityDescription;
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
	public final native String getCreationUser() /*-{
		return this.creationUser;
	}-*/;
	public final native String getCreationDate() /*-{
		return this.creationDate;
	}-*/;
	public final native String getModificationUser() /*-{
		return this.modificationUser;
	}-*/;
	public final native String getModificationDate() /*-{
		return this.modificationDate;
	}-*/;
	public final native JsAccountEntryDetail[] getDetails() /*-{
		return this.details;
	}-*/;
}
