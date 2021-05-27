package com.esferalia.aon.gwt.fiscal.client.accounting.panel;

import com.google.gwt.core.client.JavaScriptObject;

public class JsAccountEntryDetail extends JavaScriptObject {
	
	protected JsAccountEntryDetail() {
	}
	
	public final native int getDomain() /*-{
		return this.domain;
	}-*/;
	public final native int getId() /*-{
		return this.id;
	}-*/;
	public final native Integer getAccountEntry() /*-{
		return this.accountEntry;
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
}
