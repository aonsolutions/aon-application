package com.esferalia.aon.gwt.api.client.fiscal;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.incidence.JsObject;
import com.google.gwt.core.client.JavaScriptObject;

public class JsDepositConfiguration extends JavaScriptObject {

	protected JsDepositConfiguration() {}
	
	public final native String getOperation() /*-{
		return this.operation;
	}-*/;
	
	public final native AonJsArray<JsObject> getOperationOption() /*-{
		return this.operation_option;
	}-*/;

}
