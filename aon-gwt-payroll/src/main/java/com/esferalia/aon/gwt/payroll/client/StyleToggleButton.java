package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasValue;

public class StyleToggleButton extends Button implements HasValue<Boolean> {

	private String upStyle;
	private String downStyle;

	public StyleToggleButton(String upStyle, String downStyle) {
		this.upStyle = upStyle;
		this.downStyle = downStyle;
		setStyleName(upStyle); // Up
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				setValue(!isDown(), true);
			}
		});

	}

	/**
	 * Is this button down?
	 * 
	 * @return true if the button is down
	 */
	public boolean isDown() {
		String styleName = getStyleName();
		int idx = styleName.indexOf(downStyle);

		if (idx != -1) {
			if (idx == 0 || styleName.charAt(idx - 1) == ' ') {
				int last = idx + downStyle.length();
				int end = styleName.length();
				if ((last == end)
						|| ((last < end) && (styleName.charAt(last) == ' '))) {
					return true;
				}
			}
		}

		return false;
	}

	
	@Override
	public Boolean getValue() {
		return isDown();
	}
	
	@Override
	public void setValue(Boolean value) {
		setDown(value);
	}
	
	@Override
	public void setValue(Boolean value, boolean fireEvents) {
		boolean changed = setDown(value);
		if ( changed && fireEvents ) {
			ValueChangeEvent.fireIfNotEqual(this, !value, value);
		}
	}
	
	@Override
	public HandlerRegistration addValueChangeHandler(
			ValueChangeHandler<Boolean> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}


	/**
	 * Sets whether this button is down.
	 * 
	 * @param down
	 *            true to press the button, false otherwise
	 * @return true if this button was not already <b>down</b>.
	 */
	boolean setDown(boolean down) {
		if (down != isDown()) {
			toggleDown(down);
			return true;
		}
		return false;
	}

	/**
	 * Toggle the up/down attribute.
	 * 
	 * @param down
	 *            true to press the button, false otherwise
	 */
	void toggleDown(boolean down) {
		removeStyleName(down ? upStyle : downStyle);
		setStyleName(down ? downStyle : upStyle, true);
	}

}
