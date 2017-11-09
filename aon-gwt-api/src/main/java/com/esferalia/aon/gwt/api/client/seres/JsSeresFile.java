package com.esferalia.aon.gwt.api.client.seres;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsSeresFile extends JavaScriptObject {

	public static final ProvidesKey<JsSeresFile> PROVIDES_KEY = new ProvidesKey<JsSeresFile>() {
		@Override
		public Object getKey(JsSeresFile js) {
			return js == null ? null : js.getId();
		}
	};
	
	protected JsSeresFile() {}
	
	public final native String getId() /*-{
		return this.id;
	}-*/;
	
	public final native String getCode() /*-{
		return this.code;
	}-*/;
	
	public final native String getReferenceCode() /*-{
		return this.reference_code;
	}-*/;

	public final native String getDate() /*-{
		return this.date;
	}-*/;
	
	public final native String getRegistryName() /*-{
		return this.registry_name;
	}-*/;
	
	public final native String getStatus() /*-{	
		return this.status;
	}-*/;
	

}
