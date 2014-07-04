package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.TextBox;

public class IntegerTextBox extends TextBox {

	private static final AonResources AON_RESOURCES = GWT.create(AonResources.class);
	private static final CommonMessages MSG = GWT.create(CommonMessages.class);
	public static final NumberFormat FMT = NumberFormat.getFormat(MSG.integerPattern());	
	
	public IntegerTextBox() {
		addStyleName(AON_RESOURCES.css().aonTextRight() );
		
		addBlurHandler( new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if ( !isEmpty( getValue() ) ) {
					try {
						int value = Integer.parseInt( getValue());
						setValue(FMT.format(value));
						removeStyleName(AON_RESOURCES.css().aonTextBoxError() );	
						addStyleName(AON_RESOURCES.css().aonTextRight() );
					} catch (NumberFormatException e) {
						addStyleName(AON_RESOURCES.css().aonTextBoxError() );
						
					}
				} 
			}
		});

		addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				if ( !isEmpty( getValue() ) ) {
					try {
						Number value = FMT.parse(getValue()); 
						setValue( value == null ? null : value.toString() );
						removeStyleName(AON_RESOURCES.css().aonTextRight() );
					} catch (NumberFormatException e) {
						// Nothing. Value is ready for edit.
					} 
				} 
			}
		});
	}
	
	public void setValue(int value) {
		try {
			super.setValue(FMT.format(value));
			removeStyleName(AON_RESOURCES.css().aonTextBoxError() );
		} catch (NumberFormatException e) {
			addStyleName(AON_RESOURCES.css().aonTextBoxError() );
		}
	}
	
	public int getIntValue() {
		int i;
		try {
			Double d = FMT.parse(getValue()); 
			i = d.intValue();
		} catch (NumberFormatException e) {
			try {
				i = Integer.parseInt(getValue());
			} catch (NumberFormatException ex) {
				i = 0;
			}
		}
		return i;
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
	
}
