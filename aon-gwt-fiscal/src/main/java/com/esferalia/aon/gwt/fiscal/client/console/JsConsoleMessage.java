package com.esferalia.aon.gwt.fiscal.client.console;

import com.esferalia.aon.occam.api.model.console.ConsoleMessageType;
import com.google.gwt.core.client.JavaScriptObject;

public class JsConsoleMessage extends JavaScriptObject {
	
	protected JsConsoleMessage() {
	}
	
	public final native String getProcessId() /*-{
		return this.processId;
	}-*/;
	
	public final native String getTypeString() /*-{
		return this.type;
	}-*/;
	public final ConsoleMessageType getType() {
		return ConsoleMessageType.safeValueOf(getTypeString());
	}
	
	public final native String getMessage() /*-{
		return this.message;
	}-*/;
	
	public final native Integer getCount() /*-{
		return this.count;
	}-*/;
	
	public final native Integer getProgress() /*-{
		return this.progress;
	}-*/;
	
	public final native Double getPercent() /*-{
		return this.percent;
	}-*/;
	
}
