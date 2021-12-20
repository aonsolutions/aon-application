package com.esferalia.aon.gwt.fiscal.client.mod190;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.InlineLabel;

class Model190SmallerLabel extends InlineLabel {
	
	Model190SmallerLabel(String label) {
		super(label);
		setStyleName(AON.CSS.aonFontSmaller());
	}
}
