package com.esferalia.aon.gwt.issues.client;

import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class Issues implements EntryPoint {
	
	@Override
	public void onModuleLoad() {
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();


	}

}
