package com.esferalia.aon.gwt.dump.shared;

import com.google.gwt.user.client.Window;

public class Navegation {
	
	public static String getBrowserName(){
		return Window.Navigator.getUserAgent().toLowerCase();	
	}
	
	public static boolean isChromeBrowser(){
		return getBrowserName().contains("chrome");
	}
	
	public static boolean isSafariBrowser(){
		return getBrowserName().contains("safari");
	}
	
	public static boolean isFireFoxBrowser(){
		return getBrowserName().contains("firefox");
	}
	
	public static boolean isIEBrowser(){
		return getBrowserName().contains("msie");
	}
	
	
}


