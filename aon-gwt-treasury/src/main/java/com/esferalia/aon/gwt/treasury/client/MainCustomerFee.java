package com.esferalia.aon.gwt.treasury.client;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.css.AonGwtTemplateResources;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class MainCustomerFee extends MainEntryPoint{
	
	// ----------------------------------------------- UiBinder
	
	interface Binder extends UiBinder<Widget, MainCustomerFee> {}

	private static final Binder binder = GWT.create(Binder.class);
	
	// ----------------------------------------------- UiFields
	
	// ----------------------------------------------- Variables
	
	// ----------------------------------------------- Constructor

	public MainCustomerFee() {	
		Window.alert("MainCustomerFee");
		// Inject rich styles.
		AON.ensureInjected();
		GWT.<GWTResources>create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources>create(AonResources.class).css().ensureInjected();
		GWT.<AonGwtTemplateResources>create(AonGwtTemplateResources.class).css().ensureInjected();
	
		Widget ui = binder.createAndBindUi(this);
		RootLayoutPanel.get(getRootPanel() != null ? getRootPanel() : "rootPanel").add(ui);
	}
	
	// ----------------------------------------------- onModuleLoad
	
	@Override
	public void onModuleLoad() {
		Window.alert("HOLA SERGIO");
	}

}
