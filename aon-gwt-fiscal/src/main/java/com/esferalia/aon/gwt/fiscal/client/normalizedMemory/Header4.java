package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class Header4 extends ResizeComposite {

	interface Header4Binder extends UiBinder<Widget, Header4> {
	}

	private static final Header4Binder header4Binder = GWT
			.create(Header4Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	@UiField TabPanel tabPanel;
	
	
	Enterprise enterprise;
	NormalizedMemory normalizedMemory;
	public Header4(Enterprise enterprise, NormalizedMemory nm) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();
		
		tabPanel = new TabPanel();
		
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		init();
		Widget ui = header4Binder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(0);
	}


	public void init(){
		
	}
}
