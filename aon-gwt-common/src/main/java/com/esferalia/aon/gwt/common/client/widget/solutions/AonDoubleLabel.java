package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.Label;

public class AonDoubleLabel extends Label {
	
	public AonDoubleLabel() {
		this.setStyleName(AON.CSS.aonTextRight());
	}
	
	public AonDoubleLabel(Double value) {
		this(value, false);
	}

	public AonDoubleLabel(Double value, boolean emptyIfZero) {
		this();
		setValue( value , emptyIfZero);
	}
	
	public void setValue(Double value) {
		setValue(value, false);
	}

	public void setValue(Double value, boolean emptyIfZero) {
		if (value == null || (emptyIfZero && AonMathUtils.isZero(value))) {
			super.setText( AonStringUtils.EMPTY);
		} else {
			super.setText(AON.CURRENCY_FORMAT.format(value));		
		}
	}
	

}
