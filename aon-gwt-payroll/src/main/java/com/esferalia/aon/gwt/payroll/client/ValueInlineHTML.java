package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.InlineHTML;

public class ValueInlineHTML extends InlineHTML implements ValueLabel {

	private static int DEFAULT_CHANGE_DISPLAY_MILLIS = 4000;
	
	private int changeDisplayMillis = DEFAULT_CHANGE_DISPLAY_MILLIS;
	private String changeDisplayStyleName;
	private SafeHtml htmlNull ;
	

	@Override
	public void setText(String text) {
		resetStyleName();
		if ( text == null )
			super.setHTML(htmlNull);
		else
			super.setText(text);
	}
	
	@Override
	public void setText(String text, boolean displayChanges) {
		String oldText = getText();
		setText(text);
		if (shouldDisplayChange(displayChanges, oldText, text)) {
			addStyleName(changeDisplayStyleName);
			if (changeDisplayMillis > 0)
				new Timer() {
					@Override
					public void run() {
						ValueInlineHTML.this
								.removeStyleName(changeDisplayStyleName);
					}
				}.schedule(changeDisplayMillis);
		}
	}
	
	public void setHtmlNull(String htmlNull) {
		this.htmlNull = SafeHtmlUtils.fromSafeConstant(htmlNull);
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
	
	/**
	 * Convenience method to know when we should display a text change in a
	 * null-safe manner.
	 * 
	 * @param source
	 *            the source
	 * @param oldText
	 *            the old text
	 * @param newText
	 *            the new text
	 * @return whether the change should be displayed
	 */
	private boolean shouldDisplayChange(boolean displayChanges, String oldText, String newText) {
		return displayChanges && changeDisplayStyleName != null && changeDisplayMillis != 0 && oldText != newText
				&& (oldText == null || !oldText.equals(newText));
	}
}
