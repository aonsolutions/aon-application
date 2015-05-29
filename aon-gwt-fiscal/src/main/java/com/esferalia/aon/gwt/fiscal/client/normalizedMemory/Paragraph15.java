package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Paragraph15 extends ResizeComposite {

	
	interface Paragraph15Binder extends UiBinder<Widget, Paragraph15> {
	}

	private static final Paragraph15Binder binder = GWT
			.create(Paragraph15Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	public Paragraph15() {
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
