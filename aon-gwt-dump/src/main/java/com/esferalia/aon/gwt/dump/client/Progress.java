package com.esferalia.aon.gwt.dump.client;

import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.ui.FocusWidget;

public class Progress extends FocusWidget {
	
	public Progress() {
		super(Document.get().createElement("progress"));
	}
	
	public void setProgress(int progress) {
		getElement().setPropertyInt("value", progress);
	}
	
	public void setMax(int max) {
		getElement().setPropertyInt("max", max);
	}

}
