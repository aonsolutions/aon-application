package com.esferalia.aon.gwt.api.client.registry;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.JavaScriptObject;

public class JsProfile extends JavaScriptObject {

	protected JsProfile() {
	}

	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native String getQuestion() /*-{
		return this.question;
	}-*/;
	
	public final native AonJsArray<JsObject> getArray() /*-{
		return this.array;
	}-*/;
	
	
}
