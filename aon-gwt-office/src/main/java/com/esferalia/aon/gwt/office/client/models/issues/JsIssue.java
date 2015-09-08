package com.esferalia.aon.gwt.office.client.models.issues;

import java.util.Date;

import com.esferalia.aon.gwt.office.client.models.users.JsUser;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JsIssue extends JavaScriptObject {
	protected JsIssue() {
	}

	public final native int getNumber() /*-{
		return this.number;
	}-*/;

	public final native String getTitle() /*-{
		return this.title;
	}-*/;

	public final native String getUrl() /*-{
		return this.url;
	}-*/;

	public final native String getHtmlUrl() /*-{
		return this.html_url;
	}-*/;

	public final native String getState() /*-{
		return this.state;
	}-*/;

	public final native JsUser getUser() /*-{
		return this.user;
	}-*/;

	public final native Date getCreatedAt() /*-{
		return this.created_at;
	}-*/;

	public final native Date getUpdatedAt() /*-{
		return this.updated_at;
	}-*/;

	public final native String getBody() /*-{
		return this.body;
	}-*/;

	public final native int getComments() /*-{
		return this.comments;
	}-*/;

	public final native JsUser getAssignee() /*-{
		return this.assignee;
	}-*/;

	public final native int getId() /*-{
		return this.id;
	}-*/;

	public final native JsMilestone getMilestone() /*-{
		return this.milestone;
	}-*/;

	public final native JsArray<JsLabel> getLabels() /*-{
		return this.labels;
	}-*/;

}
