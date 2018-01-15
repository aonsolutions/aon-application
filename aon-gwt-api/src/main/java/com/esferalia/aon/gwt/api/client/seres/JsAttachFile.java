package com.esferalia.aon.gwt.api.client.seres;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.view.client.ProvidesKey;

public class JsAttachFile extends JavaScriptObject {

	public static final ProvidesKey<JsAttachFile> PROVIDES_KEY = new ProvidesKey<JsAttachFile>() {
		@Override
		public Object getKey(JsAttachFile js) {
			return js == null ? null : js.getId();
		}
	};
	
	protected JsAttachFile() {}
	
	public final native String getId() /*-{
		return this.id;
	}-*/;
	
	public final native int getSource() /*-{	
		return this.source;
	}-*/;
	
	public final native Integer getSourceId() /*-{	
		return this.source_id;
	}-*/;
	
	public final native String getDescription() /*-{	
		return this.description;
	}-*/;

	public final native int getType() /*-{	
		return this.type;
	}-*/;
	
	public final native String getCreationDate() /*-{
		return this.creation_date;
	}-*/;

}
