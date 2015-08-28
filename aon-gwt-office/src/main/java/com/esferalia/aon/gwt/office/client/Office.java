package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.common.client.RootLayoutPanel;
import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xhr.client.XMLHttpRequest;

public class Office extends Composite implements EntryPoint{

	private static OfficeUiBinder uiBinder = GWT.create(OfficeUiBinder.class);

	interface OfficeUiBinder extends UiBinder<Widget, Office> {}
	
	@UiField
	TabPanel tabPanel;

	public Office() {
		Widget ui = uiBinder.createAndBindUi(this);
		
		RootLayoutPanel root = RootLayoutPanel.get("rootPanel");
		root.add(ui);
		
		tabPanel.selectTab(0);
	}

	@Override
	public void onModuleLoad() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		GWT.<AonResources> create(AonResources.class).css().ensureInjected();
		
		XMLHttpRequest xhr = XMLHttpRequest.create();
		xhr.open("POST", URL.encode(GWT.getModuleBaseURL() + "OfficeServlet"));
		xhr.setRequestHeader("Content-type",
				"application/x-www-form-urlencoded");
		
		
		
	}

}
