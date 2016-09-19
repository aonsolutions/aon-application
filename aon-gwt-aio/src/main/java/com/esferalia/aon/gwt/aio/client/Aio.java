package com.esferalia.aon.gwt.aio.client;

import static com.esferalia.aon.gwt.common.client.AONEntryPoint.getParameter;

import com.esferalia.aon.gwt.aio.shared.Modules;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.shared.Constants;
import com.esferalia.aon.gwt.document.client.Documents;
import com.esferalia.aon.gwt.issues.client.Issues;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;


public class Aio implements EntryPoint {

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		
		String entryPoint = getParameter(GWT.getModuleName(), Constants.ENTRY_POINT_PARAM);
		
		switch (entryPoint) {
		case Modules.ISSUES:
			new Issues().onModuleLoad();
			break;
		case Modules.DOCUMENT:
			new Documents().onModuleLoad();
			break;
		default:
			break;
		}
	}
	
}
