package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Header5 extends ResizeComposite {

	
	interface Header5Binder extends UiBinder<Widget, Header5> {
	}

	private static final Header5Binder header5Binder = GWT
			.create(Header5Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	Enterprise enterprise;
	NormalizedMemory normalizedMemory;
	
	public Header5(Enterprise enterprise, NormalizedMemory nm) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();
		
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		
		init();
		Widget ui = header5Binder.createAndBindUi(this);
		initWidget(ui);
	}


	public void init(){
		
	}
}
