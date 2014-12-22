package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Page12 extends ResizeComposite implements RequiresResize {

	interface Page7Binder extends UiBinder<Widget, Page12> {
	}

	private static final Page7Binder page7Binder = GWT
			.create(Page7Binder.class);

	public Page12() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		Widget ui = page7Binder.createAndBindUi(this);
		initWidget(ui);
	}

	public void setValue(Mod390 m390) {
		// TODO Auto-generated method stub
		
	}

	public void populate(Mod390 mod390) {
		// TODO Auto-generated method stub
		
	}
	
}
