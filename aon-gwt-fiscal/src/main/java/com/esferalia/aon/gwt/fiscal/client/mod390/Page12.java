package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.fiscal.client.css.AonResources;
import com.esferalia.aon.gwt.fiscal.client.css.GWTResources;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;
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

	private static final AonResources RESOURCES = GWT
			.create(AonResources.class);
	
	public Page12() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

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
