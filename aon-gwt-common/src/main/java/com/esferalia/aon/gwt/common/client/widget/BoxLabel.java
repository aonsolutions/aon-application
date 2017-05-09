package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.Label;

public class BoxLabel extends Label {
	
	private static final int DEFAULT_TEXT_SIZE = 3;
	private int textSize;
	
	public BoxLabel() {
		this(null,DEFAULT_TEXT_SIZE);
	}
	public BoxLabel(String text) {
		this(text,DEFAULT_TEXT_SIZE);
	}
	public BoxLabel(int text) {
		this(AonNumberUtils.toString(text),DEFAULT_TEXT_SIZE);
	}
	
	public BoxLabel(String text, int textSize) {
		this.setStyleName(AON.AON_CSS.aonFiscalBox());
		this.textSize = textSize;
		setText(text);
	}

	
	public void removeErrorState() {
		removeStyleName(AON.AON_CSS.aonFiscalBoxError());
		setTitle(null);
	}
	
	public void addErrorState(String errorMsg) {
		addStyleName(AON.AON_CSS.aonFiscalBoxError());
		setTitle(errorMsg);
	}
	
	@Override
	public void setText(String text) {
		if (text != null && text.length() < textSize) {
			text = AonStringUtils.leftPad(text, textSize, '0');
		}
		super.setText(text);
	}
}
