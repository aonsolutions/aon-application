package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;

public class AonSplash extends HorizontalPanel{

	public AonSplash() {
		setStyleName(AON.CSS.aonBlockCenter());
		addStyleName(AON.CSS.aonMarginTop() );
		Label iconWaitLabel = new Label();
		iconWaitLabel.setStyleName(AON.CSS.aonLoader());
		iconWaitLabel.addStyleName(AON.CSS.aonMargin());
		add(iconWaitLabel);
		Label textWaitLabel = new Label( AON.MSG.processing());
		textWaitLabel.setStyleName(AON.CSS.aonMargin());
		textWaitLabel.addStyleName(AON.CSS.aonBold());
		add(textWaitLabel);
	}

}
