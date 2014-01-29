package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.user.client.ui.HasText;

public interface ValueLabel extends HasText {

	void addStyleName(String style);

	/**
	 * Sets the title associated with this object. The title is the 'tool-tip'
	 * displayed to users when they hover over the object.
	 * 
	 * @param title
	 *            the object's new title
	 */
	void setTitle(String title);

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
