package com.esferalia.aon.gwt.connect.client;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.Widget;

public class DSIImportForm implements EntryPoint {

	interface Binder extends UiBinder<Widget, DSIImportForm> {
	}

	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField FormPanel uploadFormPanel;
	@UiField FileUpload fileUpload;
	@UiField Button sendButton;
	
	public DSIImportForm() {

	}

	@Override
	public void onModuleLoad() {
		
		// Inject rich styles.
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();

		// Create the UI defined in DSIImportForm.ui.xml.
		Widget ui = binder.createAndBindUi(this);

		// Add the outer panel to the RootLayoutPanel, so that it will be
		// displayed.
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		
		init();
	}	
	
	protected void init () {
		uploadFormPanel.setAction("/aon_gwt_connect/dsiimport");
		uploadFormPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		uploadFormPanel.setMethod(FormPanel.METHOD_POST);

	}
	// ------------------------------------------------------------- UiHandlers
	
	@UiHandler ("uploadFormPanel")
	void onSubmit(SubmitEvent event) {
	
		if("".equalsIgnoreCase(fileUpload.getFilename()) == false) {
			Window.alert("Subiendo archivo");
			//NOW WHAT¿?
		}
		
		else {
			Window.alert("Subida cancelada");
			event.cancel();
		}
 	}
	
	@UiHandler ("uploadFormPanel")
	void onSubmitComplete(SubmitCompleteEvent event) {
		
		//refresh page??
		
		Window.alert("Todo ok");
	}
	
	@UiHandler ("sendButton")
	void onClick (ClickEvent event) {
		
		 try {
			 uploadFormPanel.submit();
		 }catch(Exception ex) {
			 Window.alert("Error en submit");
		 }
		 
	}

}
