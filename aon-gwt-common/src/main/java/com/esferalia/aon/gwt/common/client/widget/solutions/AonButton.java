package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.Button;

public class AonButton extends Button {

	public AonButton(String toolTip) {
		setStyleName(AON.CSS.aonButton());
		setTitle(toolTip);
	}

	public AonButton(String toolTip, String iconStyle) {
		this( toolTip );
		setIconStyle(iconStyle);
	}

	public void setIconStyle(String iconStyle ) {
		addStyleName( iconStyle );
	}

}
