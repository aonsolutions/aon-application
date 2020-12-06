package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;

public class AonToolbarButton extends AonButton {

	public AonToolbarButton(String toolTip) {
		super(toolTip);
		addStyleName(AON.CSS.aonToolbarButton());
	}

	public AonToolbarButton(String toolTip, String iconStyle) {
		super(toolTip,iconStyle);
		addStyleName(AON.CSS.aonToolbarButton());
	}
	
	public AonToolbarButton(String toolTip, String iconStyle, char accesskey) {
		super(toolTip,iconStyle,accesskey);
		addStyleName(AON.CSS.aonToolbarButton());
	}
}
