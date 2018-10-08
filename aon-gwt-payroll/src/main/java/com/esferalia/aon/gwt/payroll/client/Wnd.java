package com.esferalia.aon.gwt.payroll.client;

public class Wnd {

	public static native String getCurrentUser()
	/*-{
		return $wnd.getCurrentUser();
	}-*/;

	public static native String getCurrentDomainNameURL()
	/*-{
		return $wnd.getCurrentDomainNameURL();
	}-*/;


}
