package com.esferalia.aon.gwt.common.client.widget.solutions;

import java.util.Date;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.Label;

public class AonDateLabel extends Label {
	
	public AonDateLabel() {
	}
	
	public AonDateLabel(Date date) {
		setValue( date );
	}
	
	public void setValue(Date date) {
		if (date == null ) {
			super.setText( AonStringUtils.EMPTY);
		} else {
			super.setText(AON.DATE_FORMAT.format(date));		
		}
	}
	

}
