package com.esferalia.aon.gwt.office.client.models.users;

import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JsUserWorkgroups extends JavaScriptObject {

	protected JsUserWorkgroups() {
	}
	
	public final native JsArray<JsLabel> getLabels() /*-{
		return this.tags;
	}-*/;	

	public final native JsArray<JsUser> getUsers() /*-{
		return this.users;
	}-*/;

	public final native JsArray<JsWorkGroup> getWorkGroups() /*-{
		return this.workgroups;
	}-*/;

}
