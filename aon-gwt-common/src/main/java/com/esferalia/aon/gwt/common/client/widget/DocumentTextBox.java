package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

public class DocumentTextBox extends TextBox {

	private static final AonResources AON_RESOURCES = GWT.create(AonResources.class);
	
	public DocumentTextBox() {
		setVisibleLength(9);
		setMaxLength(9);
		setStyleName(AON_RESOURCES.css().aonInputText());
		addValueChangeHandler( new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				if (AonDocumentUtil.isValid(event.getValue())) {
					removeStyleName(AON_RESOURCES.css().aonTextBoxError() );	
				} else {
					addStyleName(AON_RESOURCES.css().aonTextBoxError() );	
				}
			}
			
		});
	}
	
	@Override
	public void setValue(String value) {
		super.setValue(value);
		if (value == null || value.length() == 0 || AonDocumentUtil.isValid(getValue()) ){
			removeStyleName(AON_RESOURCES.css().aonTextBoxError() );	
		} else {
			addStyleName(AON_RESOURCES.css().aonTextBoxError() );	
		}
	}
}
