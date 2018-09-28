package com.esferalia.aon.gwt.codemirror.client.ui;

import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.MergeView;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.ModeConfiguration;
import com.esferalia.aon.gwt.codemirror.client.ui.CodeMirror.Pos;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;

public class MergeArea extends HTML {
	
	private MergeView mergeView;
	
	private MergeView.Options options;

	
	public MergeArea() {
		options = MergeView.Options.create();
	}
	
	
	public boolean refresh() {
		if ( mergeView == null)
			return false;
		return mergeView.refresh();
		
	}
	
	public boolean resize(int height) {
		if ( mergeView == null)
			return false;
		return mergeView.resize(height);
		
	}

	// --------------------------------------------- MergeView.Options Delegate
	
	public void setMode(String mode) {
		options.setMode(mode);
	}

	public void setMode(ModeConfiguration mode) {
		options.setMode(mode);
	}

	public void setTheme(String theme) {
		options.setTheme(theme);
	}

	public void setKeyMap(String keyMap) {
		options.setKeyMap(keyMap);
	}

	public void setReadOnly(boolean readOnly) {
		options.setReadOnly(readOnly);
	}

	public void setOrig(String orig) {
		options.setOrig(orig);
	}

	public void setFoldGutter(boolean foldGutter) {
		options.setFoldGutter(foldGutter);
	}

	public void setLineNumbers(boolean lineNumbers) {
		options.setLineNumbers(lineNumbers);
	}

	public void setMatchBrackets(boolean matchBrackets) {
		options.setMatchBrackets(matchBrackets);
	}

	public void setOrigLeft(String origLeft) {
		options.setOrigLeft(origLeft);
	}

	public void setOrigRight(String origRight) {
		options.setOrigRight(origRight);
	}

	public void setRevertButtons(boolean revertButtons) {
		options.setRevertButtons(revertButtons);
	}
	
	public void setConnect(boolean connect) {
		options.setConnect(connect);
	}

	public void setShowDifferences(boolean showDifferences) {
		options.setShowDifferences(showDifferences);
	}
	
	// ----------------------------------------------------- MergeView Delegate

	public void setValue(String value) {
		if ( mergeView == null )
			options.setValue(value);
		else 
			mergeView.getEditor().setValue(value);
	}
	

	public String getValue(){
		return mergeView.getEditor().getValue();
	}

	public String getOrig() {
		return mergeView.getRightOriginal().getValue();
	}
	
	public void scrollIntoView(Pos pos) {
		mergeView.getEditor().scrollIntoView(pos);
	}
	
	
	public void setSelection(Pos anchor, Pos head) {
		mergeView.getEditor().setSelection(anchor, head);
	}
	
	// ---------------------------------------------------------- HTML Override
	
	@Override
	protected void onLoad() {
		super.onLoad();		
		mergeView = CodeMirror.mergeView(getElement(), options);
	}
	
	
}
