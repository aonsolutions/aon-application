package com.esferalia.aon.gwt.aio.client;

import com.google.gwt.core.client.JavaScriptObject;

public class JsAio extends JavaScriptObject {
	
	protected JsAio() {
	
	}
	
	public static native void addOnBeforeUnloadHandler(Aio thiz)/*-{
		$wnd.onbeforeunload = function(event){
			thiz.@com.esferalia.aon.gwt.aio.client.Aio::onBeforeUnload(*)();
		}
	}-*/;

	public static native void addOnReloadHandler(Aio thiz) /*-{
		$wnd.onreload = function() {
			thiz.@com.esferalia.aon.gwt.aio.client.Aio::onReload(*)();
		}
	}-*/;
	
}