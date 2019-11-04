package com.esferalia.aon.gwt.fiscal.client.tedi;

import com.google.gwt.core.client.JavaScriptObject;

public class JsTediCenter extends JavaScriptObject {
	
	protected JsTediCenter() {
	
	}
	
	public static native void addOnBeforeUnloadHandlerTC(TediCenter thiz)/*-{
		$wnd.onbeforeunloadtc = function(event){
			thiz.@com.esferalia.aon.gwt.fiscal.client.tedi.TediCenter::onBeforeUnloadTC(*)();
		}
	}-*/;

	public static native void addOnReloadHandlerTC(TediCenter thiz) /*-{
		$wnd.onreloadtc = function() {
			thiz.@com.esferalia.aon.gwt.fiscal.client.tedi.TediCenter::onReloadTC(*)();
		}
	}-*/;
	
}