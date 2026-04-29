package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import com.google.gwt.core.client.JavaScriptObject;

public class JsDomainInvoiceStat extends JavaScriptObject {
	
	protected JsDomainInvoiceStat() {
	}
	
	
	public final native int getId() /*-{
		return this.id;
	}-*/;
	public final native Integer getParentId() /*-{
		return this.parentId;
	}-*/;
	public final native String getName() /*-{
		return this.name;
	}-*/;
	public final native String getDescription() /*-{
		return this.description;
	}-*/;
	public final native String getCompanyDocument() /*-{
		return this.companyDocument;
	}-*/;
	public final native String getCompanyName() /*-{
		return this.companyName;
	}-*/;
	public final native boolean isActive() /*-{
		return this.active;
	}-*/;
	public final native boolean isExpired() /*-{
		return this.expired;
	}-*/;
	public final native int getNational() /*-{
		return this.national;
	}-*/;
	public final native int getIntracommunity() /*-{
		return this.intracommunity;
	}-*/;
	public final native int getExtracommunity() /*-{
		return this.extracommunity;
	}-*/;
	public final native int getCanCeuMel() /*-{
		return this.canCeuMel;
	}-*/;
	public final native int getOtherISP() /*-{
		return this.otherISP;
	}-*/;
	public final native int getWithholding() /*-{
		return this.withholding;
	}-*/;
	public final native int getUndeclaredIssued() /*-{
		return this.undeclaredIssued;
	}-*/;
	public final native int getUndeclaredReceived() /*-{
		return this.undeclaredReceived;
	}-*/;
	public final native int getUndeclaredSimplified() /*-{
		return this.undeclaredSimplified;
	}-*/;
	public final native int getProformas() /*-{
		return this.proformas;
	}-*/;
	public final native int getUnrecordedIssued() /*-{
		return this.unrecordedIssued;
	}-*/;
	public final native int getUnrecordedReceived() /*-{
		return this.unrecordedReceived;
	}-*/;
	public final native int getUnrecordedSimplified() /*-{
		return this.unrecordedSimplified;
	}-*/;
	public final native int getDraft() /*-{
		return this.draft;
	}-*/;
	public final native int getInProcess() /*-{
		return this.inProcess;
	}-*/;
	public final native int getReview() /*-{
		return this.review;
	}-*/;
	public final native int getTrash() /*-{
		return this.trash;
	}-*/;
	
	
}
