package com.esferalia.aon.gwt.codemirror.client.ui;

import java.util.Map;

import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.ChangeEvent;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.Configuration;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.Doc;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.DocumentChangeHandler;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.ModeConfiguration;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.Pos;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.Token;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.TextArea;

public class CodeArea extends TextArea {

	private CodeMirror codeMirror;
	private Configuration config ;
	

	public CodeArea() {
		config = Configuration.create();
	}

	public CodeArea(Element element) {
		super(element);
		config = Configuration.create();
	}
	
	// ------------------------------------------------------------------------
	
	public void setTheme(String theme) {
		config.setTheme(theme);
	}

	public void setMode(String mode) {
		config.setMode(mode);
	}
	
	public void setMode(ModeConfiguration mode) {
		config.setMode(mode);
	}

	public void setFoldGutter(boolean foldGutter) {
		config.setFoldGutter(foldGutter);
	}


	public void setLineNumbers(boolean lineNumbers) {
		config.setLineNumbers(lineNumbers);
	}

	public void setMatchBrackets(boolean matchBrackets) {
		config.setMatchBrackets(matchBrackets);
	}
	

	// ------------------------------------------------------------------------
	

	
	public final Doc getDoc() {
		return codeMirror.getDoc();
	}
	
	public void refresh() {
		codeMirror.refresh();
	}

	public void scrollIntoView(Pos pos) {
		codeMirror.scrollIntoView(pos);
	}
	
	public void setSelection(Pos anchor, Pos head) {
		codeMirror.setSelection(anchor, head);
	}

	public final void addOverlay(String mode) {
		codeMirror.addOverlay(mode);
	}
	
	public final void setSize(int width, int height) {
		codeMirror.setSize(width, height);
	}

	public final void addKeyMap(String key, String action) {
		codeMirror.addKeyMap(key, action);
	}

	public final void addKeyMap(Map<String, String> map) {
		codeMirror.addKeyMap(map);
	}
	
	public final Token getTokenAt(Pos pos, boolean precise) {
		return codeMirror.getTokenAt(pos, precise);
	}

	public final <M extends JavaScriptObject> M getMode() {
		return codeMirror.getMode();
	}
	
	public final void addDocumentChangeHandler(DocumentChangeHandler handler) {
		codeMirror.addDocumentChangeHandler(handler);
	}

	public final void removeDocumentChangeHandler(DocumentChangeHandler handler) {
		codeMirror.removeDocumentChangeHandler(handler);
	}

	// ------------------------------------------------------------------------
	@Override
	public void setValue(String value, boolean fireEvents) {
		codeMirror.setValue(value);
		super.setValue(value, fireEvents);
	}
	
	
	
	@Override
	protected void onLoad() {
		super.onLoad();
		codeMirror = CodeMirror.fromTextArea(TextAreaElement.as(getElement()), config);
		
		codeMirror.getWrapperElement().addClassName(getStyleName());

		
		addDocumentChangeHandler(new DocumentChangeHandler() {
			@Override
			public void handleEvent(Doc source, ChangeEvent event) {
				CodeArea.super.setValue(codeMirror.getValue(), true);
				CodeArea.this.codeMirror.refresh();
			}
		});
		
	}
	// ------------------------------------------------------------------------
	
	

	
}
