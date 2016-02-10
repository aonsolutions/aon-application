package com.esferalia.aon.gwt.viewer.client;

public class PrintWindow {
	
	 public static native void open(String url, String name, String features) /*-{
	    var printWnd = $wnd.open(url, name, features);
	    printWnd.print();
	  }-*/;
}
