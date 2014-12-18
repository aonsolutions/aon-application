package com.esferalia.aon.gwt.fiscal.client.mod390;

import com.esferalia.aon.gwt.common.client.css.AonResources;
import com.esferalia.aon.gwt.common.client.css.GWTResources;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.occam.api.model.Mod390;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Page1 extends ResizeComposite {

	interface Page1Binder extends
			UiBinder<Widget, Page1> {
	}

	private static final Page1Binder page1Binder = GWT
			.create(Page1Binder.class);

	private final static AonResources RESOURCES = GWT.create(AonResources.class);
	
	@UiField
	CheckBox taxRefund;
	@UiField
	CheckBox specialGroupRegime;
	@UiField
	TextBox groupNumber;
	@UiField
	CheckBox groupDependent;
	@UiField
	CheckBox groupDeclarations;
	@UiField
	CheckBox insolvencyDeclarations;
	@UiField
	CheckBox groupRegimeType;
	@UiField
	DocumentTextBox groupDocument;
	
	public Page1() {
		GWT.<GWTResources> create(GWTResources.class).css().ensureInjected();
		RESOURCES.css().ensureInjected();
		
		Widget ui = page1Binder.createAndBindUi(this);
		initWidget(ui);
	}
	public void setValue(Mod390 m390) {
		taxRefund.setValue(m390.isTaxRefund());
		groupNumber.setValue(m390.getGroupNumber());
		groupDependent.setValue(m390.isGroupDependent());
		groupDeclarations.setValue(m390.isGroupDeclarations());
		insolvencyDeclarations.setValue(m390.isInsolvencyDeclarations());
		groupDocument.setValue(m390.getGroupDocument());
		groupDeclarations.setValue(m390.isGroupDeclarations());
		specialGroupRegime.setValue(m390.isSpecialGroupRegime(),true);
		onClickSpecialGroupRegime(null);
	}

	@UiHandler("specialGroupRegime")
	void onClickSpecialGroupRegime(ClickEvent event) {
		groupNumber.setEnabled(specialGroupRegime.getValue());
		groupDependent.setEnabled(specialGroupRegime.getValue());
		groupRegimeType.setEnabled(specialGroupRegime.getValue());
		groupDocument.setEnabled(specialGroupRegime.getValue());
		groupDeclarations.setEnabled(specialGroupRegime.getValue());
		
		if (!specialGroupRegime.getValue()) {
			groupNumber.setValue(null);
			groupDependent.setValue(false);
			groupRegimeType.setValue(false);
			groupDocument.setValue(null);
			groupDeclarations.setValue(false);
		}
	}
	
	public void populate(Mod390 mod390) {
		mod390.setInsolvencyDeclarations(insolvencyDeclarations.getValue());
		mod390.setTaxRefund(taxRefund.getValue());
		mod390.setSpecialGroupRegime(specialGroupRegime.getValue());
		mod390.setGroupNumber( groupNumber.getValue());
		mod390.setGroupDependent( groupDependent.getValue());
		mod390.setGroupRegimeType(groupRegimeType.getValue());
		mod390.setGroupDocument( groupDocument.getValue());
		mod390.setGroupDeclarations( groupDeclarations.getValue());
	}
}
