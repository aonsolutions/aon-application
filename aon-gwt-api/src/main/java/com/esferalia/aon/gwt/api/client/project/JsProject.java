package com.esferalia.aon.gwt.api.client.project;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.registry.JsRegistry;
import com.google.gwt.core.client.JavaScriptObject;

public class JsProject extends JavaScriptObject{

	public static JavaScriptObject create() {
		return JavaScriptObject.createObject().cast();
	}

	protected JsProject() {

	}

	public final native int getId() /*-{
		return this.id;
	}-*/;
	
	public final native int getDomain() /*-{
		return this.domain;
	}-*/;
	
	public final native String getName() /*-{
		return this.name;
	}-*/;

	public final native String getAlias() /*-{
		return this.alias;
	}-*/;
	
	public final native JsRegistry getRegistry() /*-{
		return this.registry;
	}-*/;

	public final native String getDate() /*-{
		return this.date;
	}-*/;
	

	public final native int getProjectType() /*-{
		return this.project_type;
	}-*/;
	

	public final native JsRegistry getSeller() /*-{
		return this.seller;
	}-*/;
	
	public final native String getComment() /*-{
		return this.comment;
	}-*/;
	
	public final native AonJsArray<JsCommercialTracking> getCommercialTrackingList() /*-{
		return this.commercial_tracking;
	}-*/;
	
}
