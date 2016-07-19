package com.esferalia.aon.gwt.api.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;

public class AonJsArray<T extends JavaScriptObject> extends JsArray<T>{
	  
	  protected AonJsArray() {
	  }
	public final native AonJsArray<T> concat(AonJsArray<T> array) /*-{
	    return this.concat(array);
	 }-*/;
}