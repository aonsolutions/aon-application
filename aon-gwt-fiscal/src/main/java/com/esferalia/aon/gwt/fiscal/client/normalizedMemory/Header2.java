package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Header2 extends ResizeComposite {
	
	@UiField
	TextBox a_notes;
	@UiField
	TextBox a_current;
	@UiField
	TextBox a_previous;
	

	interface Header2Binder extends UiBinder<Widget, Header2> {
	}

	private static final Header2Binder header2Binder = GWT
			.create(Header2Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	public Header2() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = header2Binder.createAndBindUi(this);
		initWidget(ui);
	}

	public void setValue(NormalizedMemory memory) {
	}

	public void populate(NormalizedMemory memory) {
	}
	
}
