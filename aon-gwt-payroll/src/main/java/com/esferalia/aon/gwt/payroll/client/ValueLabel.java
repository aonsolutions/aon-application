package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.user.client.ui.HasText;

public interface ValueLabel extends HasText {

	/**
	 * Sets this object's text. Displays changes when displayChanges is true and
	 * the new text does not equal the existing text.
	 * 
	 * @param text
	 *            the object's new text
	 * @param displayChanges
	 *            display changes if true and text is new
	 */
	void setText(String text, boolean displayChanges);

}
