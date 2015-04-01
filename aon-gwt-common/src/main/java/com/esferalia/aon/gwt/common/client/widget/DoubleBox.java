package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.AON;

public class DoubleBox extends com.google.gwt.user.client.ui.DoubleBox {

	private static final int VISIBLE_LENGTH = 15;
	private static final int MAX_LENGTH = 15;

	
	public DoubleBox() {
		super();
		setVisibleLength(VISIBLE_LENGTH);
		setMaxLength(MAX_LENGTH);
		setStyleName(AON.AON_CSS.aonInputText());
	}
}
