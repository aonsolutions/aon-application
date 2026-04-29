package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.Label;

public class AonIntegerLabel extends Label {
	
	public AonIntegerLabel() {
		this.setStyleName(AON.CSS.aonTextRight());
	}
	
	public AonIntegerLabel(Integer value) {
		this(value, false);
	}

	public AonIntegerLabel(Integer value, boolean emptyIfZero) {
		this();
		setValue( value , emptyIfZero);
	}
	public AonIntegerLabel(Integer value, String defIfZero) {
		this();
		setValue( value , defIfZero);
	}
	
	public void setValue(Integer value) {
		setValue(value, false);
	}

	public void setValue(Integer value, boolean emptyIfZero) {
		if (value == null || (emptyIfZero && AonMathUtils.isZero(value))) {
			super.setText( AonStringUtils.EMPTY);
		} else {
			super.setText(AON.FMT_INT.format(value));		
		}
	}
	public void setValue(Integer value, String defIfZero) {
		if (value == null || (defIfZero != null && AonMathUtils.isZero(value))) {
			super.setText( defIfZero );
		} else {
			super.setText(AON.FMT_INT.format(value));		
		}
	}
	

}
