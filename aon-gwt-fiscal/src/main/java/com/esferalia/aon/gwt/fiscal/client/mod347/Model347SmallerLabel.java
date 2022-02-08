package com.esferalia.aon.gwt.fiscal.client.mod347;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.InlineLabel;

class Model347SmallerLabel extends InlineLabel {
	
	Model347SmallerLabel(String label) {
		super();
		if (AonStringUtils.length(label) > 35) {
			setText(AonStringUtils.abbreviate(label, 35));
			setTitle(label);
		} else {
			setText(label);
		}
		setStyleName(AON.CSS.aonFontSmaller());
	}
}
