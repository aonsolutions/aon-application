package com.esferalia.aon.gwt.issues.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class StatPanel extends Composite {
	
	interface Binder extends UiBinder<Widget, StatPanel> {

	}
	private static final Binder binder = GWT.create(Binder.class);
	
	public static native String getCurrentDomainName()
	/*-{
		return $wnd.getCurrentDomainName();
	}-*/;

	public static native int getCurrentDomain()
	/*-{
		return $wnd.getCurrentDomain();
	}-*/;
	
	public StatPanel() {
		
	}
	
}
