package com.esferalia.aon.gwt.office.client.models.issues;

import com.esferalia.aon.gwt.office.client.AonHub;
import com.esferalia.aon.gwt.office.client.models.AJSON;
import com.esferalia.aon.gwt.office.client.models.users.JsUser;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class JsIssue extends JavaScriptObject {

	public static JavaScriptObject create() {
		return JavaScriptObject.createObject().cast();
	}

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

	public final native String getState() /*-{
		return this.state;
	}-*/;

	public final native String getCompany() /*-{
		return this.company;
	}-*/;

	public final native String getSource() /*-{
		return this.source;
	}-*/;	

	public final native String getPriority() /*-{
		return this.priority;
	}-*/;

	public final native String getType() /*-{
		return this.type;
	}-*/;

	public final native JsUser getUser() /*-{
		return this.user;
	}-*/;

	public final native String getCreatedAt() /*-{
		return this.created_at;
	}-*/;

	public final native String getUpdatedAt() /*-{
		return this.updated_at;
	}-*/;

	public final native String getBody() /*-{
		return this.body;
	}-*/;

	public final native int getComments() /*-{
		return this.comments;
	}-*/;

	public final native String getCommentsUrl() /*-{
		return this.comments_url;
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

	public final void getCommments(
			final AsyncCallback<AJSON<JsArray<JsIssueComment>>> callback) {

		final String commentsUrl = getCommentsUrl();
		AonHub.get(commentsUrl, callback);
	}
}
