package com.esferalia.aon.gwt.api.client.sii;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.JavaScriptObject;

public class JsSiiConfiguration extends JavaScriptObject {

	protected JsSiiConfiguration() {}
	
	public final native String getOperationDate() /*-{
		return this.operation_date;
	}-*/;
	
	public final native AonJsArray<JsObject> getOperationDateOption() /*-{
		return this.operation_date_option;
	}-*/;

}
