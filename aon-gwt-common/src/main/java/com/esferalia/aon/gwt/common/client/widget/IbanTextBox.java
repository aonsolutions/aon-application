package com.esferalia.aon.gwt.common.client.widget;


import com.esferalia.aon.gwt.common.client.i18n.CommonMessages;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.TextBox;

public class IbanTextBox extends TextBox {

	private static final CommonMessages MSG = GWT.create(CommonMessages.class);
	public static final NumberFormat FMT = NumberFormat.getFormat(MSG.decimalPattern(),MSG.currencyCode());	
	
	private static final int VISIBLE_LENGTH = 25;
	private static final int MAX_LENGTH = 20;
	

	public IbanTextBox() {
		this(VISIBLE_LENGTH);
	}
	
	public IbanTextBox(int visibleLength) {
		setVisibleLength(visibleLength);
		setMaxLength(MAX_LENGTH);
	}
	
}
