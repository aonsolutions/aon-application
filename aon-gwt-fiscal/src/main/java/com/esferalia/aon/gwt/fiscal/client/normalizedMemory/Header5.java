package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
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

	public Header5() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = header5Binder.createAndBindUi(this);
		initWidget(ui);
	}

	public void setValue(NormalizedMemory memory) {
	}

	public void populate(NormalizedMemory memory) {
	}
}
