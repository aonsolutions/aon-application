package com.esferalia.aon.gwt.office.client.models.issues;

import java.util.Date;

import com.esferalia.aon.gwt.office.client.models.users.User;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

public class IssueComment extends JavaScriptObject {

	protected IssueComment() {
	}

	public final native String getUrl() /*-{
		return this.url;
	}-*/;

	public final native String getBody() /*-{
		return this.body;
	}-*/;

	public final native User getUser() /*-{
		return this.user;
	}-*/;

	public final Date getCreatedAt() {
		return DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_SHORT)
				.parse(this.getCreatedAtString());
	}

	public final native String getCreatedAtString() /*-{
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
