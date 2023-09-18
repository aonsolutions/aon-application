package com.esferalia.aon.gwt.common.client.widget;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.Style.Display;

public abstract class Upload {
	
	
	public Upload() {
	
	}
	
	protected abstract void onUpload(String data, String type);
	
	public void upload() {
		Document doc = Document.get();
		InputElement input = doc.createFileInputElement();
		input.getStyle().setDisplay(Display.NONE);
		files(input, this);
		input.click();
	}
	
	private final native void files(Element input, Upload thiz) /*-{
		input.addEventListener("change", function(event) {
		var reader = new FileReader();
		var file = input.files[0];
		var type = file.type;
		reader.readAsDataURL(file);
			reader.onload = function() {
				var base64File = reader.result.split(',')[1];
				thiz.@com.esferalia.aon.gwt.common.client.widget.Upload::onUpload(*)(base64File, type);
			}
		});
	}-*/;
}
