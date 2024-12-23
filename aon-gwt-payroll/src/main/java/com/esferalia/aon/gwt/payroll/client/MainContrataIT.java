package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.google.gwt.core.client.GWT;

public class MainContrataIT extends MainEntryPoint {
	
	private ITWidget itWidget;
	
	// --------------------------------------------------- Constructor
	
	public MainContrataIT() {
		// Inject rich styles.
		AON.ensureInjected();
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<MainEntryPoint.CodeMirrorResources>create(MainEntryPoint.CodeMirrorResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();

		
		itWidget = new ITWidget();
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(itWidget);
	}
	
	// --------------------------------------------------- OnModuleLoad
	
	@Override
	public void onModuleLoad() {
		itWidget.loadITWidget();
	}
	
}
