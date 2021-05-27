package com.esferalia.aon.gwt.dump.client;

import java.util.Arrays;

import com.esferalia.aon.gwt.common.shared.StringUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.Document;
import com.vaadin.polymer.Polymer;
import com.vaadin.polymer.iron.IronIconsElement;
import com.vaadin.polymer.iron.IronLabelElement;
import com.vaadin.polymer.iron.IronListElement;
import com.vaadin.polymer.paper.PaperDialogElement;
import com.vaadin.polymer.paper.PaperIconButtonElement;
import com.vaadin.polymer.paper.PaperInputElement;
import com.vaadin.polymer.paper.PaperSliderElement;
import com.vaadin.polymer.paper.PaperTextareaElement;
import com.vaadin.polymer.paper.PaperToggleButtonElement;
import com.vaadin.polymer.vaadin.VaadinComboBoxElement;

import net.aonsolutions.polymer.aon.AonComboBoxElement;

public class MainEntryPoint implements EntryPoint {

	@Override
	public void onModuleLoad() {
		
		
		Polymer.importHref(Arrays.asList(
				IronIconsElement.SRC,
				PaperInputElement.SRC,
				PaperTextareaElement.SRC,
				PaperDialogElement.SRC,
				VaadinComboBoxElement.SRC,
				AonComboBoxElement.SRC,
				PaperIconButtonElement.SRC,
				IronListElement.SRC,
				PaperToggleButtonElement.SRC,
				PaperSliderElement.SRC,
				IronLabelElement.SRC
		));
		
		Polymer.whenReady(o -> {
			ensureGwtSelector();
			DSIImportForm dsiImportForm = new DSIImportForm();
			dsiImportForm.onModuleLoad();
			return null;
		});
		
	}
	
	public static native String getParameter(String moduleName,
			String parameterName) /*-{
		var search = "/" + moduleName + ".nocache.js";
		var scripts = $doc.getElementsByTagName("script");
		for (var i = 0; i < scripts.length; ++i) {
			if (scripts[i].src != null && scripts[i].src.indexOf(search) != -1) {
				var params = scripts[i].src.match(/\w+=\w+/g);
				for (var j = 0; j < params.length; ++j) {
					var keyvalue = params[j].split("=");
					if (keyvalue.length == 2 && keyvalue[0] == parameterName) {
						return unescape(keyvalue[1]);
					}
				}
			}
		}
		return null;
	}-*/;

	public static void ensureGwtSelector() {
		BodyElement body = Document.get().getBody();
		String className = body.getClassName();
		if (StringUtils.isBlank(className)
				|| (className.indexOf("gwt-Selector") == -1))
			body.addClassName("gwt-Selector");

	}


}
