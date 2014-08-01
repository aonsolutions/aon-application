package com.esferalia.aon.gwt.codemirror.client.mode;

import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.Configuration;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.ModeConfiguration;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.Stream;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.regexp.shared.RegExp;

public final class AONOverlayMode extends JavaScriptObject {

	public static AONOverlayMode create() {
		AONOverlayMode aonMode = JavaScriptObject.createObject().cast();
		return aonMode.exportDefine().exportToken();
	}

	protected AONOverlayMode() {
	}

	// --------------------------------------------------------------------
	public native AONOverlayMode exportToken()/*-{
		this.token = @com.esferalia.aon.gwt.codemirror.client.mode.AONOverlayMode::token(*);
		return this;
	}-*/;


	private native AONOverlayMode exportDefine() /*-{
		this.define = @com.esferalia.aon.gwt.codemirror.client.mode.AONOverlayMode::define(*);
		return this;
	}-*/;

	// --------------------------------------------------------------------

	private static RegExp READ_ONLY = RegExp
			.compile("(/\\*(user|read-only)\\*/)((?:[^/]|(?:/[^\\*]))*)(/\\*\\*/)");

	public static final String token(Stream stream) {
		JsArrayString match = stream.match(READ_ONLY);
		if ( match != null )
			return match.get(2);

		stream.next();
		
		if ( stream.skipTo("/") )
			return "expression";
		
		stream.skipToEnd();
		
		return "expression";
	}

	public static final JavaScriptObject define(Configuration config,
			ModeConfiguration modeConfig) {
		
		return AONOverlayMode.create();
	}

}