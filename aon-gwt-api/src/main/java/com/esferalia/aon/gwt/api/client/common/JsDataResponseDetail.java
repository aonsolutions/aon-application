package com.esferalia.aon.gwt.api.client.common;

import com.google.gwt.core.client.JavaScriptObject;

public class JsDataResponseDetail extends JavaScriptObject {

	protected JsDataResponseDetail() {}
	
	public final native Integer getId() /*-{
		return this.id;
	}-*/;
	
	public final native Integer getDomain() /*-{
		return this.domain;
	}-*/;
	
	public final native Integer getDataResponse() /*-{
		return this.data_response;
	}-*/;
	
	public final native String getDataVariable() /*-{
		return this.data_variable;
	}-*/;
	
	public final native String getValue() /*-{
		return this.value;
	}-*/;
	
	public final native String getCreationUser() /*-{
		return this.creation_user;
	}-*/;

	public final native String getCreationDate() /*-{
		return this.creation_date;
	}-*/;

	public final native String getModificationUser() /*-{
		return this.modification_user;
	}-*/;

	public final native String getModificationDate() /*-{
		return this.modification_date;
	}-*/;

}
