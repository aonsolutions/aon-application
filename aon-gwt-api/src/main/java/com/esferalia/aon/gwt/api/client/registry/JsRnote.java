package com.esferalia.aon.gwt.api.client.registry;

import com.google.gwt.core.client.JavaScriptObject;

public class JsRnote extends JavaScriptObject {

	protected JsRnote() {
	}

	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native Integer getDomain() /*-{
		return this.domain;
	}-*/;
	
	public final native Integer getRegistry() /*-{
		return this.registry;
	}-*/;

	public final native String getDescription() /*-{
		return this.description;
	}-*/;
	
	public final native String getNoteDate() /*-{
		return this.note_date;
	}-*/;
	
	public final native String getComments() /*-{
		return this.comments;
	}-*/;
	
	public final native Integer getNoteType() /*-{
		return this.note_type;
	}-*/;
	
	public final native Integer isConfidential() /*-{
		return this.confidential;
	}-*/;

}
