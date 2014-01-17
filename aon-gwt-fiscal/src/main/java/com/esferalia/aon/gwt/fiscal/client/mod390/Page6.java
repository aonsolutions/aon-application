package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.fiscal.client.css.AonResources;
import com.esferalia.aon.gwt.fiscal.client.css.GWTResources;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Page6 extends ResizeComposite implements RequiresResize {

	interface Page6Binder extends UiBinder<Widget, Page6> {
	}

	private static final Page6Binder page6Binder = GWT
			.create(Page6Binder.class);

	private static final AonResources RESOURCES = GWT
			.create(AonResources.class);
	
	public Page6() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = page6Binder.createAndBindUi(this);
		initWidget(ui);
	}

	public void setValue(Mod390 m390) {
		// TODO Auto-generated method stub
		
	}

	public void populate(Mod390 mod390) {
		// TODO Auto-generated method stub
		
	}
	
}
