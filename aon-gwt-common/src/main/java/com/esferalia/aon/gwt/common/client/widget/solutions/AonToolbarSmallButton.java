package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;

public class AonToolbarSmallButton extends AonButton {

	public AonToolbarSmallButton(String toolTip) {
		super(toolTip);
		addStyleName(AON.CSS.aonToolbarSmallButton());
	}

	public AonToolbarSmallButton(String toolTip, String iconStyle) {
		super(toolTip,iconStyle);
		addStyleName(AON.CSS.aonToolbarSmallButton());
	}
	
	public AonToolbarSmallButton(String toolTip, String iconStyle, char accesskey) {
		super(toolTip,iconStyle,accesskey);
		addStyleName(AON.CSS.aonToolbarSmallButton());
	}
}
