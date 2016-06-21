package com.esferalia.aon.gwt.api.client.incidence;

import com.google.gwt.core.client.JavaScriptObject;

public class JsIssue extends JavaScriptObject{

	public static JavaScriptObject create() {
		return JavaScriptObject.createObject().cast();
	}

	protected JsIssue() {
	}

	public final native int getId() /*-{
		return this.id;
	}-*/;
	
	public final native String getTitle() /*-{
		return this.title;
	}-*/;

	public final native String getUrl() /*-{
		return this.url;
	}-*/;
	
	public final native String getRepositoryUrl() /*-{
		return this.repository_url;
	}-*/;
	
	public final native String getLabelsUrl() /*-{
		return this.labels_url;
	}-*/;

	public final native String getCommentsUrl() /*-{
		return this.comments_url;
	}-*/;
	
	public final native int getNumber() /*-{
		return this.number;
	}-*/;
	
	public final native String getState() /*-{
		return this.state;
	}-*/;
	
	public final native String getBody() /*-{
		return this.body;
	}-*/;
	
	public final native String getClosedAt() /*-{
		return this.closed_at;
	}-*/;
	
	public final native String getCreatedAt() /*-{
		return this.created_at;
	}-*/;
	
	public final native String getUpdatedAt() /*-{
		return this.updated_at;
	}-*/;
	
	public final native JsUser getAssignee() /*-{
		return this.assignee;
	}-*/;

}
