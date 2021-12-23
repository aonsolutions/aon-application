package com.esferalia.aon.gwt.fiscal.client.mod180;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.InlineLabel;

class Model180SmallerLabel extends InlineLabel {
	
	Model180SmallerLabel(String label) {
		super(label);
		setStyleName(AON.CSS.aonFontSmaller());
	}
}
