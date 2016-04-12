package com.esferalia.aon.gwt.office.client.notification;

import com.esferalia.aon.occam.api.model.Domain;

public class JsNotification {
	
	public static Domain getDomain(){
		return new Domain().setId(getCurrentDomain()).setName(getCurrentDomainName());
	}
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
}
