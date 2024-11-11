package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;

public class AonCustomButton extends HTMLPanel {

	public AonCustomButton(String iconStyle, String message) {
		super("");
		this.setStyleName(AON.CSS.aonFlexBetween());
		this.getElement().getStyle().setProperty("cursor", "pointer");
		this.getElement().getStyle().setProperty("border", "1px solid #b9b8b8");
		this.getElement().getStyle().setProperty("padding", ".3rem");
		
		this.add(new AonTableButton(message, iconStyle));
		this.add(new Label(message));
		
	}
	
}
