package com.esferalia.aon.gwt.api.client.incidence;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsObject extends JavaScriptObject {

	protected JsObject() {}

	public static final ProvidesKey<JsObject> PROVIDES_KEY = new ProvidesKey<JsObject>() {
		@Override
		public Object getKey(JsObject object) {
			return object == null ? null : object.getId();
		}
	};
	
	
	public final native Integer getId() /*-{
		return this.id;
	}-*/;

	public final native String getName() /*-{
		return this.name;
	}-*/;
	
	public final native String getDate() /*-{
		return this.date;
	}-*/;

}
