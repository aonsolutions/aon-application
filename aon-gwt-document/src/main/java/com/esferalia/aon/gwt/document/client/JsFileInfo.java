package com.esferalia.aon.gwt.document.client;

import com.google.gwt.core.client.JavaScriptObject;

public class JsFileInfo extends JavaScriptObject {
	protected JsFileInfo() {
	}

	public final native String getIcon() /*-{
		return this.icon;
	}-*/;

	public final native String getTitle() /*-{
		return this.title;
	}-*/;
	
	public final native int getFileId() /*-{
		return this.fileId;
	}-*/;

	public final native String getDriveId() /*-{
		return this.driveId;
	}-*/;

	public final native byte getMimetype() /*-{
		return this.mimetype;
	}-*/;
}