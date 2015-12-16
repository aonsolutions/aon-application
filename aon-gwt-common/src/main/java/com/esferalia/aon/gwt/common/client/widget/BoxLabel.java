package com.esferalia.aon.gwt.common.client.widget;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.Label;

public class BoxLabel extends Label {
	
	private static final int TEXT_SIZE = 3;

	public BoxLabel() {
		super();
		this.setStyleName(AON.AON_CSS.aonFiscalBox());
	}

	public BoxLabel(String text) {
		super(text);
		this.setStyleName(AON.AON_CSS.aonFiscalBox());
	}
	public BoxLabel(int number) {
		this(AonNumberUtils.toString(number));
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
		if (text != null && text.length() < TEXT_SIZE) {
			text = AonStringUtils.leftPad(text, TEXT_SIZE, '0');
		}
		super.setText(text);
	}
}
