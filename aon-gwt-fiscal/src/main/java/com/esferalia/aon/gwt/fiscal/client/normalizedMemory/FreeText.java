package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;

public class FreeText extends ResizeComposite {

	@UiField
	Label title1;
	@UiField
	Label title2;
	@UiField
	TextArea value;
	
	interface FreeTextBinder extends UiBinder<Widget, FreeText> {
	}

	private static final FreeTextBinder binder = GWT
			.create(FreeTextBinder.class);

	private final static AonResources RESOURCES = GWT
			.create(AonResources.class);

	public FreeText(String pageHeader, boolean isFreeText) {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();

		Widget ui = binder.createAndBindUi(this);
		initWidget(ui);
		
		if(isFreeText){
			title1.setText("MEMORIA ABREVIADA - TEXTO LIBRE");
		} else {
			title1.setText("MEMORIA ABREVIADA - MODELO DE RESPUESTA NORMALIZADA");
		}
		title2.setText(pageHeader);
	}

	public void setValue(NormalizedMemory memory) {
	}

	public void populate(NormalizedMemory memory) {
	}
}
