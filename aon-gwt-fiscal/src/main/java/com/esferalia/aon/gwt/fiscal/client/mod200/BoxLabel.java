package com.esferalia.aon.gwt.fiscal.client.mod200;

import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.RESOURCES;

import com.esferalia.aon.gwt.common.shared.AonUtil;
import com.google.gwt.user.client.ui.Label;

public class BoxLabel extends Label {
	
	private static final int TEXT_SIZE = 3;

	public BoxLabel() {
		super();
		this.setStyleName(RESOURCES.css().aonFiscalBox());
	}

	public BoxLabel(String text) {
		super(text);
		this.setStyleName(RESOURCES.css().aonFiscalBox());
	}
	
	public void removeErrorState() {
		removeStyleName(RESOURCES.css().aonFiscalBoxError());
		setTitle(null);
	}
	
	public void addErrorState(String errorMsg) {
		addStyleName(RESOURCES.css().aonFiscalBoxError());
		setTitle(errorMsg);
	}
	
	@Override
	public void setText(String text) {
		if (text != null && text.length() < TEXT_SIZE) {
			text = AonUtil.leftPad(text, TEXT_SIZE, '0');
		}
		super.setText(text);
	}
}
