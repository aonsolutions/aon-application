package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;

public class AonSearchPanelButton extends AonButton {

	public AonSearchPanelButton(String toolTip) {
		super(toolTip);
		addStyleName(AON.CSS.aonSearchPanelButton());
	}
	
	public AonSearchPanelButton(String toolTip, String iconStyle) {
		super(toolTip,iconStyle);
		addStyleName(AON.CSS.aonSearchPanelButton());
	}

}
