package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.OptGroupElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.user.client.ui.ListBox;

public class AonListBox extends ListBox {
	

	public void addGroup(String text) {
	    SelectElement select = getElement().cast();
	    OptGroupElement option = Document.get().createOptGroupElement();
	    option.setLabel(text);
	    option.setTitle(text);
        select.appendChild(option);
	}

}