package com.esferalia.aon.gwt.fiscal.client.widget;

import com.esferalia.aon.gwt.fiscal.client.css.AonResources;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SimplifiedRegimePanel extends ResizeComposite {

	private final AonResources aonResources = GWT.create(AonResources.class);

	interface SimplifiedRegimePanelBinder extends
			UiBinder<Widget, SimplifiedRegimePanel> {
	}

	private static final SimplifiedRegimePanelBinder enterpriseBinder = GWT
			.create(SimplifiedRegimePanel.class);

	@UiField
	Label title;
	
	@UiField
    TextBox epigrafe;
	@UiField
    DoubleTextBox unit1;
	@UiField
    DoubleTextBox amount1;
	@UiField
    DoubleTextBox unit2;
	@UiField
    DoubleTextBox amount2;
	@UiField
    DoubleTextBox unit3;
	@UiField
    DoubleTextBox amount3;
	@UiField
    DoubleTextBox unit4;
	@UiField
    DoubleTextBox amount4;
	@UiField
    DoubleTextBox unit5;
	@UiField
    DoubleTextBox amount5;
	@UiField
    DoubleTextBox unit6;
	@UiField
    DoubleTextBox amount6;
	@UiField
    DoubleTextBox unit7;
	@UiField
    DoubleTextBox amount7;
	@UiField
    DoubleTextBox cuotaDevengada;
	@UiField
    DoubleTextBox lorca2013;
	@UiField
    DoubleTextBox cuotaSoportada;
	@UiField
    DoubleTextBox indiceCorrector;
	@UiField
    DoubleTextBox resultado;
	@UiField
    DoubleTextBox porcCuotaMinima;
	@UiField
    DoubleTextBox devCuotaSopOtrosPaises;
	@UiField
    DoubleTextBox cuotaMinima;
	@UiField
    DoubleTextBox cuotaRegSimplificado;
	
	
	
	public SimplifiedRegimePanel() {
		aonResources.css().ensureInjected();
		
		Widget ui = enterpriseBinder.createAndBindUi(this);
		initWidget(ui);
	}

}
