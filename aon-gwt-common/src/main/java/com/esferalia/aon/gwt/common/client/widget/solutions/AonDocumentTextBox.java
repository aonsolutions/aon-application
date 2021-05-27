package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.TextBox;

public class AonDocumentTextBox extends TextBox {
	
	private boolean validation;
	
	public AonDocumentTextBox() {
		this(true);	
	}
	
	public AonDocumentTextBox(boolean validation) {
		this.validation = validation;
		setVisibleLength(11);
		setMaxLength(15);
		setStyleName(AON.CSS.aonInputText());
		if (validation) {
			addValueChangeHandler( new ValueChangeHandler<String>() {
	
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					if (AonDocumentUtil.isValid(event.getValue())) {
						removeStyleName(AON.CSS.aonInputTextError());	
					} else {
						addStyleName(AON.CSS.aonInputTextError() );	
					}
				}
				
			});
		}
	}
	
	@Override
	public void setValue(String value) {
		super.setValue(value);
		if (this.validation) {
			if (value == null || value.length() == 0 || AonDocumentUtil.isValid(getValue()) ){
				removeStyleName(AON.CSS.aonInputTextError() );	
			} else {
				addStyleName(AON.CSS.aonInputTextError() );	
			}
		}
	}
}
