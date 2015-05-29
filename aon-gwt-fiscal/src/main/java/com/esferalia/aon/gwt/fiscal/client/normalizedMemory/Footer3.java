package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Footer3 extends ResizeComposite {

	
	interface Footer3Binder extends UiBinder<Widget, Footer3> {
	}

	private static final Footer3Binder binder = GWT
			.create(Footer3Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	public Footer3() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
	}

	public void setValue(NormalizedMemory memory) {
	}

	public void populate(NormalizedMemory memory) {
	}
}
