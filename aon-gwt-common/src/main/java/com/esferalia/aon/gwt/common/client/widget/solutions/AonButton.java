package com.esferalia.aon.gwt.common.client.widget.solutions;

import com.esferalia.aon.gwt.common.client.AON;
import com.google.gwt.user.client.ui.Button;

public class AonButton extends Button {

	public static final char AON_ACCESSKEY_RESET = 'N';
	public static final char AON_ACCESSKEY_SAVE = 'G';
	public static final char AON_ACCESSKEY_DELETE = 'B';

	public AonButton(String toolTip) {
		this( toolTip, null , ' ' );
	}

	public AonButton(String toolTip, String iconStyle) {
		this( toolTip ,iconStyle, ' ');
	}
	
	public AonButton(String toolTip, String iconStyle,char accesskey) {
		setStyleName(AON.CSS.aonButton());
		if ( iconStyle != null) {
			addStyleName( iconStyle );
		}
		if (accesskey != ' ') {
			setAccessKey(accesskey);
			toolTip = toolTip + "[" +  accesskey + "]";		
		}
		setTitle(toolTip);
	}
}