package com.esferalia.aon.gwt.codemirror.client.mode;

import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.ModeConfiguration;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public final class XMLConfiguration extends ModeConfiguration {

	public static XMLConfiguration create() {
		return JavaScriptObject.createObject().cast();
	}

	protected XMLConfiguration() {
	}

}