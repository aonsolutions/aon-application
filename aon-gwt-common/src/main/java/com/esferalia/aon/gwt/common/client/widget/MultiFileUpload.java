package com.esferalia.aon.gwt.common.client.widget;

import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.FileUpload;

public class MultiFileUpload extends FileUpload {
	 
	@UiConstructor
	public MultiFileUpload() {
		this.getElement().setAttribute("multiple", "multiple");
	}
	
	public void setAccept(String accept) {
		this.getElement().setAttribute("accept", accept);
	}
	
}

