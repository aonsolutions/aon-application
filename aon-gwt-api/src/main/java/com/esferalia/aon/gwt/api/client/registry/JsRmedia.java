package com.esferalia.aon.gwt.api.client.registry;

import com.google.gwt.core.client.JavaScriptObject;

public class JsRmedia extends JavaScriptObject {

	protected JsRmedia() {
	
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

	public final native Integer getMedia() /*-{
		return this.media;
	}-*/;
	
	public final native String getValue() /*-{
		return this.value;
	}-*/;
	
	public final native String getComment() /*-{
		return this.comment;
	}-*/;
	
	public final native Boolean isAdministrative() /*-{
		return this.administrative;
	}-*/;
	
	public final native Boolean isCommercial() /*-{
		return this.commercial;
	}-*/;
	
	public final native Boolean isTechnical() /*-{
		return this.technical;
	}-*/;
	
	public final native Integer getRaddress() /*-{
		return this.raddress;
	}-*/;
	
	public final native String getIcon() /*-{
		return this.icon;
	}-*/;	
}
