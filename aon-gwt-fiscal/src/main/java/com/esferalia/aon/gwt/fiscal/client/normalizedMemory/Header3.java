package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Header3 extends ResizeComposite {
	
	@UiField
	TextBox _1_notes;
	@UiField
	TextBox _1_current;
	@UiField
	TextBox _1_previous;
	

	interface Header3Binder extends UiBinder<Widget, Header3> {
	}

	private static final Header3Binder header3Binder = GWT
			.create(Header3Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	public Header3() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = header3Binder.createAndBindUi(this);
		initWidget(ui);
	}

	public void setValue(NormalizedMemory memory) {
	}

	public void populate(NormalizedMemory memory) {
	}
	
}
