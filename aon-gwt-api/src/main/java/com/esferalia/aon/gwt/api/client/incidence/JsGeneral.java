package com.esferalia.aon.gwt.api.client.incidence;

import com.esferalia.aon.gwt.api.client.AonJsArray;
import com.esferalia.aon.gwt.api.client.registry.JsRmedia;
import com.google.gwt.core.client.JavaScriptObject;

public class JsGeneral extends JavaScriptObject {

	protected JsGeneral() {}
	
	public final native String getDirection() /*-{
		return this.direction;
	}-*/;

	public final native String getCommercial() /*-{
		return this.commercial;
	}-*/;
	
	public final native String getSegmentation() /*-{
		return this.segmentation;
	}-*/;
	
	public final native AonJsArray<JsRmedia> getRmedia() /*-{
		return this.rmedia;
	}-*/;

}
