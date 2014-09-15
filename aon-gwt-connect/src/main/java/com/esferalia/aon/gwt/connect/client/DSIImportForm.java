package com.esferalia.aon.gwt.connect.client;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class DSIImportForm implements EntryPoint {

	interface Binder extends UiBinder<Widget, DSIImportForm> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@Override
	public void onModuleLoad() {
		Window.alert("Inject rich styles");
		// Inject rich styles.
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(
				AonResources.class).css().ensureInjected();
		
		Window.alert(" Create the UI defined in DSIImportForm.ui.xml");
		// Create the UI defined in DSIImportForm.ui.xml.
		Widget ui = binder.createAndBindUi(this);

		Window.alert("Add the outer panel to the RootLayoutPanel, so that it will be displayed");
		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);

		Window.alert("That's all. Folks!!!");
	}

}
