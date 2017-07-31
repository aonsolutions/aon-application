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
	
	public final native int getParent() /*-{
		return this.parent;
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
	
	public final native String getClosedAtDate() /*-{
		return this.closed_at_date;
	}-*/;

	public final native String getClosedAtHour() /*-{
		return this.closed_at_hour;
	}-*/;

	public final native String getCreatedAt() /*-{
		return this.created_at;
	}-*/;
	
	public final native String getCreatedAtDate() /*-{
		return this.created_at_date;
	}-*/;
	
	public final native String getCreatedAtHour() /*-{
		return this.created_at_hour;
	}-*/;
	
	public final native String getUpdatedAt() /*-{
		return this.updated_at;
	}-*/;
	
	public final native String getUpdatedAtDate() /*-{
		return this.updated_at_date;
	}-*/;
	
	public final native String getUpdatedAtHour() /*-{
		return this.updated_at_hour;
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
	
	public final native JsObject getSource() /*-{
		return this.source;
	}-*/;
	
	public final native Integer getSourceId() /*-{
		return this.source_id;
	}-*/;

	public final native Boolean isClosed() /*-{
		return this.is_closed;
	}-*/;

	public final native Boolean isOpen() /*-{
		return this.is_open;
	}-*/;

	public final native Boolean isDeleted() /*-{
		return this.is_deleted;
	}-*/;
	
	public final native Boolean isDuplicate() /*-{
		return this.is_duplicate;
	}-*/;
	
	public final native Boolean isPrincipalDuplicate() /*-{
		return this.is_principal_duplicate;
	}-*/;

	
	public final native String getColor() /*-{
		return this.color;
	}-*/;

	public final native Boolean isFaq() /*-{
		return this.is_faq;
	}-*/;

	public final native Boolean isFaqItem() /*-{
		return this.is_faq_item;
	}-*/;
	
	public final native Boolean isGithub() /*-{
		return this.is_github;
	}-*/;
	
	public final native String getDays() /*-{
		return this.days;
	}-*/;

}
