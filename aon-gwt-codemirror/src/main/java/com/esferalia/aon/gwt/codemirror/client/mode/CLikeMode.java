package com.esferalia.aon.gwt.codemirror.client.mode;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;

public final class CLikeMode extends JavaScriptObject {
	protected CLikeMode() {
	}
	
	public CLikeMode setBuiltin(String builtin []){
		return setBuiltin(CLikeConfiguration.words(builtin));
	}
	
	public CLikeMode setKeywords(String keywords []){
		return setKeywords(CLikeConfiguration.words(keywords));
	}

	public native CLikeMode setBuiltin(JsArrayString builtin) /*-{
		this.builtin = builtin;
		return this;
	}-*/;

	public native CLikeMode setKeywords(JsArrayString keywords) /*-{
		this.keywords = keywords;
		return this;
	}-*/;
	
	
}