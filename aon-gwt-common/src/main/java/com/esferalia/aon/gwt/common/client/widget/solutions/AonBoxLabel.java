package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.Label;

public class AonBoxLabel extends Label {
	
	private static final int DEFAULT_TEXT_SIZE = 3;
	private int textSize;
	
	public AonBoxLabel() {
		this(null,DEFAULT_TEXT_SIZE);
	}
	public AonBoxLabel(String text) {
		this(text,DEFAULT_TEXT_SIZE);
	}
	public AonBoxLabel(int text) {
		this(AonNumberUtils.toString(text),DEFAULT_TEXT_SIZE);
	}
	
	public AonBoxLabel(String text, int textSize) {
		this.setStyleName(AON.CSS.aonFiscalBox());
		this.textSize = textSize;
		setText(text);
	}

	
	public void removeErrorState() {
		removeStyleName(AON.CSS.aonFiscalBoxError());
		setTitle(null);
	}
	
	public void addErrorState(String errorMsg) {
		addStyleName(AON.CSS.aonFiscalBoxError());
		setTitle(errorMsg);
	}
	
	@Override
	public void setText(String text) {
		if (text != null && text.length() < textSize && AonStringUtils.isNumeric(text)) {
			text = AonStringUtils.leftPad(text, textSize, AonStringUtils.ZERO);
		}
		super.setText(text);
	}
}
