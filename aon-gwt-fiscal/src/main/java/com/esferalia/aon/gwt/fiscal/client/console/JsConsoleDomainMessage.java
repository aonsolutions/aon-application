package com.esferalia.aon.gwt.fiscal.client.console;

import com.esferalia.aon.occam.api.model.console.ConsoleDomainMessageType;
import com.google.gwt.core.client.JavaScriptObject;

public class JsConsoleDomainMessage extends JavaScriptObject {
	
	protected JsConsoleDomainMessage() {
	}
	
	public final native String getTypeString() /*-{
		return this.type;
	}-*/;
	public final ConsoleDomainMessageType getType() {
		return ConsoleDomainMessageType.safeValueOf(getTypeString());
	}
	
	public final native String getSchema() /*-{
		return this.schema;
	}-*/;
	
	public final native Integer getDomainId() /*-{
		return this.domainId;
	}-*/;
	
	public final native String getTable() /*-{
		return this.table;
	}-*/;

	public final native Integer getPkId() /*-{
		return this.pkId;
	}-*/;
	
	public final native String getPkCode() /*-{
		return this.pkCode;
	}-*/;
	
	public final native String getFkTable() /*-{
		return this.fkTable;
	}-*/;

	public final native String getFkColumn() /*-{
		return this.fkColumn;
	}-*/;

	public final native Integer getFkId() /*-{
		return this.fkId;
	}-*/;
	
	public final native Integer getWrongDomainId() /*-{
		return this.wrongDomainId;
	}-*/;
	
	public final native String getMessage() /*-{
		return this.message;
	}-*/;
	
}
