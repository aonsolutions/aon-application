package com.esferalia.aon.gwt.api.client.documental;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsAttach extends JavaScriptObject{

	public static JavaScriptObject create() {
		return JavaScriptObject.createObject().cast();
	}

	protected JsAttach() {
		
	}

	public static final ProvidesKey<JsAttach> PROVIDES_KEY = new ProvidesKey<JsAttach>() {
		@Override
		public Object getKey(JsAttach attach) {
			return attach == null ? null : attach.getId();
		}
	};
	
	public final native int getId() /*-{
		return this.id;
	}-*/;
	
	public final native String getTitle() /*-{
		return this.title;
	}-*/;
	
	public final native String getUrl() /*-{
		return this.url;
	}-*/;

}
