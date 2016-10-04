package com.esferalia.aon.gwt.api.client.incidence;

import com.google.gwt.core.client.JavaScriptObject;

public class JsNotify extends JavaScriptObject {

	protected JsNotify() {}

	public final native String getUrl() /*-{
		return this.url;
	}-*/;
	
	public final native String getMail() /*-{
		return this.mail;
	}-*/;

	public final native String getLogo() /*-{
		return this.logo;
	}-*/;
	
	public final native String getSign() /*-{
		return this.sign;
	}-*/;
	
	public final native String getOpen() /*-{
		return this.open;
	}-*/;
	
	public final native String getClose() /*-{
		return this.close;
	}-*/;
	
	public final native String getReopen() /*-{
		return this.reopen;
	}-*/;
	
	public final native String getComment() /*-{
		return this.comment;
	}-*/;
	
	public final native String getCommentHistory() /*-{
		return this.comment_history;
	}-*/;
	
	public final native String getStatusHistory() /*-{
		return this.status_history;
	}-*/;
	
	public final native String getBcc() /*-{
		return this.bcc;
	}-*/;
	
	public final native String getMode() /*-{
		return this.mode;
	}-*/;
	
}
