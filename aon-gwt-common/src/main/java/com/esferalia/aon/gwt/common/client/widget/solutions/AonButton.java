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
			toolTip = toolTip + " [" + getAccesskeyPrefix() + accesskey + "]";		
		}
		setTitle(toolTip);
	}

	private native String getAccesskeyPrefix() /*-{
		try {
			var isWindows = (navigator.userAgent.indexOf("Win")!=-1);
			var isMacOS = (navigator.userAgent.indexOf("Mac")!=-1); 
			var isLinux = (navigator.userAgent.indexOf("Linux")!=-1);
			var isChrome = (navigator.userAgent.indexOf("Chrome") != -1);
			var isFirefox = (navigator.userAgent.indexOf("Firefox") != -1 );
			var isEdge = (navigator.userAgent.indexOf("Edge")!= -1 );
			var isSafari = (navigator.userAgent.indexOf("Safari")!= -1 );
			
			if ( isWindows || isLinux) {
				return "ALT+SHIFT+";
			}
			if ( isMacOS) {
				if (isFirefox) return "CTRL+SHIFT+";
				if (isChrome) return "CTRL+OPT+";
				if (isSafari) return "CTRL+ALT+";
			}
			return "";
		} catch (error) {
			console.error(error);
			return "";
		}
	}-*/;
}