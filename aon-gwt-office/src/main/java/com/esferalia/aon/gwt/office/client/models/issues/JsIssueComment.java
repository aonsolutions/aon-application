package com.esferalia.aon.gwt.office.client.models.issues;

import java.util.Date;

import com.esferalia.aon.gwt.office.client.models.users.JsUser;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

public class JsIssueComment extends JavaScriptObject {

	protected JsIssueComment() {
	}

	public final native int getId() /*-{
		return this.id;
	}-*/;

	public final native String getBody() /*-{
		return this.body;
	}-*/;

	public final native JsUser getUser() /*-{
		return this.user;
	}-*/;

	public final native String getCreateAt() /*-{
		return this.created_at;
	}-*/;

	public final Date getUpdatedAt() {
		return DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT)
				.parse(this.getUpdatedAtString());
	}

	public final native String getUpdatedAtString() /*-{
		return this.updated_at;
	}-*/;
}
