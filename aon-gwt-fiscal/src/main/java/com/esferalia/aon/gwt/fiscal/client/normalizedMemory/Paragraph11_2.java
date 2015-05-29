package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;

public class Paragraph11_2 extends ResizeComposite {

	
	interface Paragraph11_2Binder extends UiBinder<Widget, Paragraph11_2> {
	}

	private static final Paragraph11_2Binder binder = GWT
			.create(Paragraph11_2Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	public Paragraph11_2() {
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
