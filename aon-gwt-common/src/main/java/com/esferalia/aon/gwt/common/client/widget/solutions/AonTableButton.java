package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;

public class AonTableButton extends AonButton {

	public AonTableButton(String toolTip) {
		super(toolTip);
		addStyleName(AON.CSS.aonTableButton());
	}
	
	public AonTableButton(String toolTip, String iconStyle) {
		super(toolTip,iconStyle);
		addStyleName(AON.CSS.aonTableButton());
	}

	public AonTableButton(String toolTip, String iconStyle, char accessKey) {
		super(toolTip,iconStyle,accessKey);
		addStyleName(AON.CSS.aonTableButton());
	}

}
