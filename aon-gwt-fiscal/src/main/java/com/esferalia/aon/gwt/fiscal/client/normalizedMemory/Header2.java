package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Header2 extends ResizeComposite {
	
	@UiField
	TextBox a_notes;
	@UiField
	TextBox a_current;
	@UiField
	TextBox a_previous;
	@UiField TabPanel tabPanel;
	

	interface Header2Binder extends UiBinder<Widget, Header2> {
	}

	private static final Header2Binder header2Binder = GWT
			.create(Header2Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);
	
	Enterprise enterprise;
	NormalizedMemory normalizedMemory;

	public Header2(Enterprise enterprise, NormalizedMemory nm) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();
		
		tabPanel = new TabPanel();
		
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		
		init();
		Widget ui = header2Binder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(0);
	}

	public void init(){
		
	}
	
}
