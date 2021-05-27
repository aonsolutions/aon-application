package com.esferalia.aon.gwt.codemirror.client.mode;

import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.ModeConfiguration;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public final class CLikeConfiguration extends ModeConfiguration {

	public static CLikeConfiguration create() {
		return JavaScriptObject.createObject().cast();
	}

	protected CLikeConfiguration() {
	}

	public final CLikeConfiguration setBuiltin(String str) {
		return setBuiltin(words(str));
	};

	public final CLikeConfiguration setBuiltin(String... words) {
		return setBuiltin(words(words));
	};

	public final native CLikeConfiguration setBuiltin(JsArrayString builtin)/*-{
		this.builtin = builtin;
		return this;
	}-*/;

	public final CLikeConfiguration setKeywords(String str) {
		return setKeywords(words(str));
	};

	public final CLikeConfiguration setKeywords(String... words) {
		return setKeywords(words(words));
	};

	public final native CLikeConfiguration setKeywords(
			JsArrayString keywords)/*-{
		this.keywords = keywords;
		return this;
	}-*/;

	public final native static JsArrayString words(String str)/*-{
		var obj = {}, words = str.split(" ");
		for (var i = 0; i < words.length; ++i)
			obj[words[i]] = true;
		return obj;
	}-*/;

	public final native static JsArrayString words(String... words)/*-{
		var obj = {};
		for (var i = 0; i < words.length; ++i)
			obj[words[i]] = true;
		return obj;
	}-*/;
}