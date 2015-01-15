package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.TextBox;

public class DoubleTextBox extends TextBox {
	private static int DEFAULT_CHANGE_DISPLAY_MILLIS = 4000;

	private static final AonResources AON_RESOURCES = GWT.create(AonResources.class);
	private static final CommonMessages MSG = GWT.create(CommonMessages.class);
	public static final NumberFormat FMT = NumberFormat.getFormat(MSG.decimalPattern(),MSG.currencyCode());	
	
	private static final int VISIBLE_LENGTH = 15;
	private static final int MAX_LENGTH = 15;
	
	private int changeDisplayMillis = DEFAULT_CHANGE_DISPLAY_MILLIS;
	private String changeDisplayStyleName;

	public DoubleTextBox() {
		this(VISIBLE_LENGTH);
	}
	
	public DoubleTextBox(int visibleLength) {
		setVisibleLength(visibleLength);
		setMaxLength(MAX_LENGTH);
		addStyleName(AON_RESOURCES.css().aonTextRight() );
		
		addBlurHandler( new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if ( !isEmpty( getValue() ) ) {
					try {
						Double value = Double.parseDouble( getValue());
						DoubleTextBox.this.setValue(FMT.format(value),false);
						removeStyleName(AON_RESOURCES.css().aonInputError() );
						addStyleName(AON_RESOURCES.css().aonTextRight() );
					} catch (NumberFormatException e) {
						addStyleName(AON_RESOURCES.css().aonInputError() );
					}
				} 
			}
		});
		
		addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent event) {
				if ( !isEmpty( getValue() ) ) {
					resetStyleName();
					removeStyleName(AON_RESOURCES.css().aonTextRight() );
					Number value = null;
					try {
						value = FMT.parse(getValue()); 
					} catch (NumberFormatException e) {
						// Nothing. Value is ready for edit.
					} 
					DoubleTextBox.this.setValue( value == null ? null : value.toString(), false );
				} 
			}
			
		});
	}
	
	public void setValue(double value) {
		setValue(value,false,true,FMT);
	}
	public void setValue(double value,NumberFormat formatter) {
		setValue(value,false,true,formatter);	
	}
	public void setValue(double value,boolean fireEvents) {
		setValue(value,fireEvents,true,FMT);
	}
	public void setValue(double value,boolean fireEvents,boolean displayChanges,NumberFormat formatter) {
		try {
			String oldText = getText();
			String text = formatter.format(value);
			super.setValue(text,fireEvents);
			if (shouldDisplayChange(displayChanges, oldText, text)) {
				addStyleName(changeDisplayStyleName);
				if (changeDisplayMillis > 0)
					new Timer() {
						@Override
						public void run() {
							DoubleTextBox.this.removeStyleName(changeDisplayStyleName);
						}
					}.schedule(changeDisplayMillis);
			}
			removeStyleName(AON_RESOURCES.css().aonInputError() );
		} catch (NumberFormatException e) {
			addStyleName(AON_RESOURCES.css().aonInputError() );
		}
	}
	public boolean isValidValue() {
		try {
			FMT.parse(getValue());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public double getDoubleValue(NumberFormat formatter) {
		double d;
		try {
			d = Double.parseDouble(getValue());
		} catch (NumberFormatException e) {
			try {
				d = formatter.parse(getValue());	
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
	
	public void setChangeDisplayStyleName(String styleName) {
		changeDisplayStyleName = styleName;
	}

	public void setChangeDisplayMillis(int changeDisplayMillis) {
		this.changeDisplayMillis = changeDisplayMillis;
	}
	
	private void resetStyleName(){
		if ( changeDisplayStyleName != null )
			removeStyleName(changeDisplayStyleName);
	}

	private boolean shouldDisplayChange(boolean displayChanges, String oldText, String newText) {
		return displayChanges && changeDisplayStyleName != null && changeDisplayMillis != 0 && oldText != newText
				&& (oldText == null || !oldText.equals(newText));
	}
	
}
