package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

public class DocumentTextBox extends TextBox {

	public DocumentTextBox() {
		setVisibleLength(11);
		setMaxLength(15);
		setStyleName(AON.AON_CSS.aonInputText());
		addValueChangeHandler( new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if (AonDocumentUtil.isValid(event.getValue())) {
					removeStyleName(AON.AON_CSS.aonTextBoxError() );	
				} else {
					addStyleName(AON.AON_CSS.aonTextBoxError() );	
				}
			}
			
		});
	}
	
	@Override
	public void setValue(String value) {
		super.setValue(value);
		if (value == null || value.length() == 0 || AonDocumentUtil.isValid(getValue()) ){
			removeStyleName(AON.AON_CSS.aonTextBoxError() );	
		} else {
			addStyleName(AON.AON_CSS.aonTextBoxError() );	
		}
	}
}
