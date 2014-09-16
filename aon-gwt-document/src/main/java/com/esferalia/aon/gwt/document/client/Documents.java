package com.esferalia.aon.gwt.document.client;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class Documents implements EntryPoint {

	interface Binder extends UiBinder<Widget, Documents> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField
	Button newButton;
	
	@UiField
	SplitLayoutPanel splitLayoutPanel;
	
	@Override
	public void onModuleLoad() {

		// Inject rich styles.
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(
				AonResources.class).css().ensureInjected();
		
		// Create the UI defined in DSIImportForm.ui.xml.
		Widget ui = binder.createAndBindUi(this);
		
		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		
	}
	
	@UiHandler("newButton")
	void XXXXXX(ClickEvent event){
		Window.alert("JJJJJJJJJJJJJJJJJJJJJJJJJ");
	}
	
}
