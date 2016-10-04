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
import com.google.gwt.user.client.rpc.AsyncCallback;


public class Aio implements EntryPoint {
	
	final IAioAsync impl = GWT.create(IAio.class);
	
	private Issues issues;
	private Documents documents;

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		
		String entryPoint = getParameter(GWT.getModuleName(), Constants.ENTRY_POINT_PARAM);
		
		switch (entryPoint) {
		case Modules.ISSUES:
			JsAio.addOnBeforeUnloadHandler(this);
			JsAio.addOnReloadHandler(this);
			issues = new Issues();
			issues.onModuleLoad();
			break;
		case Modules.DOCUMENT:
			JsAio.addOnBeforeUnloadHandler(this);
			JsAio.addOnReloadHandler(this);
			documents = new Documents();
			documents.onModuleLoad();
			break;
		default:
			break;
		}
	}
	
	public void onBeforeUnload(){
		impl.selectedMenu(new AsyncCallback<Void>() {
			@Override public void onSuccess(Void result) {}
			@Override public void onFailure(Throwable caught) {}
		});
	}
	
	public void onReload(){
		String entryPoint = getParameter(GWT.getModuleName(), Constants.ENTRY_POINT_PARAM);
		if(entryPoint.equals(Modules.ISSUES)) issues.remove();
		if(entryPoint.equals(Modules.DOCUMENT)) documents.remove();
		onModuleLoad();
	}
	
}
