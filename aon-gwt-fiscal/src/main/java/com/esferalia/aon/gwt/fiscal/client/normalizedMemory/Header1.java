package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Header1 extends ResizeComposite {

	@UiField
	TextBox municipalityName;
	@UiField
	TextBox provinceName;
	@UiField
	TextBox zipCode;
	@UiField
	TextBox phone;
	@UiField
	TextBox groupMainEnterpriseName;
	@UiField
	TextBox groupMainEnterpriseDocument;
	@UiField
	TextBox groupLastEnterpriseName;
	@UiField
	TextBox groupLastEnterpriseDocument;
	@UiField
	TextBox enterpriseMainActivity;
	@UiField
	TextBox cnaeCode;
	@UiField
	TextBox fixedCurrentAvg;
	@UiField
	TextBox fixedPreviousAvg;
	@UiField
	TextBox noFixedCurrentAvg;
	@UiField
	TextBox noFixedPreviousAvg;
	@UiField
	TextBox disabilityCurrentAvg;
	@UiField
	TextBox disabilityPreviousAvg;

	interface Header1Binder extends UiBinder<Widget, Header1> {
	}

	private static final Header1Binder header1Binder = GWT
			.create(Header1Binder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	public Header1() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = header1Binder.createAndBindUi(this);
		initWidget(ui);
	}

	public void setValue(NormalizedMemory memory) {

	}

	public void populate(NormalizedMemory memory) {

	}
	
}
