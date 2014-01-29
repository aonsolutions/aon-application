package com.esferalia.aon.gwt.fiscal.client.widget;


import com.esferalia.aon.gwt.fiscal.client.FiscalMessages;
import com.esferalia.aon.gwt.fiscal.client.css.AonResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.TextBox;

/**
 * @author ecastellano
 *
 */
public class DoubleTextBox extends TextBox {

	private static final AonResources AON_RESOURCES = GWT.create(AonResources.class);
	private static final FiscalMessages MSG = GWT.create(FiscalMessages.class);
	private static final NumberFormat FMT = NumberFormat.getFormat(MSG.decimalPattern(),MSG.currencyCode());	
	
	public DoubleTextBox() {
		setVisibleLength(15);
		setMaxLength(15);
		addStyleName(AON_RESOURCES.css().aonTextRight() );
		
		addBlurHandler( new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if ( !isEmpty( getValue() ) ) {
					try {
						Double value = Double.parseDouble( getValue());
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
	public void setValue(double value) {
		setValue(value,false,FMT);
	}
	public void setValue(double value,NumberFormat formatter) {
		setValue(value,false,formatter);	
	}
	public void setValue(double value,boolean fireEvents) {
		setValue(value,fireEvents,FMT);
	}
	public void setValue(double value,boolean fireEvents,NumberFormat formatter) {
		try {
			super.setValue(formatter.format(value),fireEvents);
			removeStyleName(AON_RESOURCES.css().aonTextBoxError() );
		} catch (NumberFormatException e) {
			addStyleName(AON_RESOURCES.css().aonTextBoxError() );
		}
	}
	
	public double getDoubleValue(NumberFormat formatter) {
		double d;
		try {
			d = formatter.parse(getValue());	
		} catch (NumberFormatException e) {
			try {
				d = Double.parseDouble(getValue());
			} catch (NumberFormatException ex) {
				d = 0.0;
			}
		}
		return d;
	}
	public double getDoubleValue() {
		return getDoubleValue(FMT);
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
	
}
