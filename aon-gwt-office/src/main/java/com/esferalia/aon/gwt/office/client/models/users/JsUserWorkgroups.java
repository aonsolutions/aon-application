package com.esferalia.aon.gwt.office.client.models.users;

import com.esferalia.aon.gwt.office.client.models.issues.JsLabel;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class JsUserWorkgroups extends JavaScriptObject {

	protected JsUserWorkgroups() {
	}

	public final native String getUser() /*-{
		return this.user;
	}-*/;

	public final native String getEnterprise() /*-{
		return this.enterprise;
	}-*/;

	public final native JsArray<JsIdentification> getIdentificacions() /*-{
		return this.identifications;
	}-*/;

	public final native JsArray<JsLabel> getPriorities() /*-{
		return this.priorities;
	}-*/;

	public final native JsArray<JsLabel> getLabels() /*-{
		return this.tags;
	}-*/;

	public final native JsArray<JsUser> getUsers() /*-{
		return this.users;
	}-*/;

	// public final native JsArray<JsWorkGroup> getWorkGroups() /*-{
	// return this.workgroups;
	// }-*/;

}
