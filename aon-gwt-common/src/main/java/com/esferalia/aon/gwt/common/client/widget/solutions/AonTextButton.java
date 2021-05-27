package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;

public class AonTextButton extends AonButton {

	public AonTextButton(String text) {
		super(text);
		setText(text);
		addStyleName(AON.CSS.aonTextButton());
	}
	
	public AonTextButton(String text, String iconStyle) {
		super(text,iconStyle);
		setText(text);
		addStyleName(AON.CSS.aonTextButton());
	}

	public AonTextButton(String text, String iconStyle, char accessKey) {
		super(text,iconStyle,accessKey);
		setText(text);
		addStyleName(AON.CSS.aonTextButton());
	}

}
