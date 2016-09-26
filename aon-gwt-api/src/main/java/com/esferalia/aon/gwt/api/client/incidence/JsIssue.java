package com.esferalia.aon.gwt.api.client.incidence;

import com.esferalia.aon.gwt.api.client.AonJsArray;
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
	
	public final native String getEventsUrl() /*-{
		return this.events_url;
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
	
	public final native JsUser getUser() /*-{
		return this.user;
	}-*/;
	
	public final native AonJsArray<JsLabel> getLabels() /*-{
		return this.labels;
	}-*/;
	
	public final native int getComments() /*-{
		return this.comments;
	}-*/;
	
	//************ BD ***************//
	
	public final native JsLabel getType() /*-{
		return this.type;
	}-*/;	
	
	public final native JsLabel getPriority() /*-{
		return this.priority;
	}-*/;
	
	public final native JsUser getWorkgroup() /*-{
		return this.workgroup;
	}-*/;
	
	public final native JsUser getEnterprise() /*-{
		return this.enterprise;
	}-*/;
}
