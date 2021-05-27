package com.esferalia.aon.gwt.fiscal.client.mod390.e2018;

import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2018.Model3902018.IMod3902018CallBack;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2018.Model3902018.IMod3902018Page;
import com.esferalia.aon.occam.api.model.fiscal.Mod3902018;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DeckPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Page00 extends ResizeComposite implements IMod3902018Page {

	IMod3902018CallBack callback;
	
	@UiField
	DeckPanel page0Panel;
	@UiField
	FlowPanel panel0;
	@UiField
	FlowPanel panel1;
	
	@UiField
	DocumentTextBox document;
	@UiField
	TextBox name;
	@UiField
	TextBox firstSurname;
	@UiField
	TextBox secondSurname;
	@UiField
	TextBox phone;
	@UiField
	CheckBox replacement;
	@UiField
	TextBox replacedReceipt;
	@UiField
	CheckBox replacementDueInsolvencyState;
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
	
	@UiField
	CheckBox insolvencyStateThisYear;
	@UiField
	CheckBox accrualRegime;
	@UiField
	CheckBox accrualRegimeTarget;
	
	interface PageBinder extends UiBinder<Widget, Page00> {}

	private static final PageBinder BINDER = GWT.create(PageBinder.class);

	public Page00(Mod3902018 m390) {
		Widget ui = BINDER.createAndBindUi(this);
		initWidget(ui);
		document.setEnabled(false);
		setValue(m390);
	}

	private void setValue(Mod3902018 m390) {
		document.setValue(m390.getDocument());
		name.setValue(m390.getName());
		replacement.setValue(m390.isReplacement());
		replacedReceipt.setValue(m390.getReplacedReceipt());
		replacementDueInsolvencyState.setValue(m390.isReplacementDueInsolvencyState());
		if (m390.isLegalEntity()) {
			name.setMaxLength(37);
			firstSurname.setValue(null);
			secondSurname.setValue(null);
			firstSurname.setEnabled(false);
			secondSurname.setEnabled(false);
		} else {
			name.setMaxLength(15);
			firstSurname.setValue(m390.getFirstSurname());
			secondSurname.setValue(m390.getSecondSurname());
			firstSurname.setEnabled(true);
			secondSurname.setEnabled(true);
		}
		phone.setValue(m390.getContactPhone());
		taxRefund.setValue(m390.isTaxRefund());
		groupNumber.setValue(m390.getGroupNumber());
		groupDependent.setValue(m390.isGroupDependent());
		groupDeclarations.setValue(m390.isGroupDeclarations());
		
		page0Panel.showWidget(page0Panel.getWidgetIndex(panel1));
		insolvencyStateThisYear.setValue(m390.isInsolvencyStateThisYear());
		accrualRegime.setValue(m390.isAccrualRegime());
		accrualRegimeTarget.setValue(m390.isAccrualRegimeTarget());
		groupRegimeType.setValue(m390.isGroupRegimeType());
		groupDocument.setValue(m390.getGroupDocument());
		groupDeclarations.setValue(m390.isGroupDeclarations());
		specialGroupRegime.setValue(m390.isSpecialGroupRegime(),true);
		onClickSpecialGroupRegime(null);
	}

	@Override
	public void populate(Mod3902018 mod390) {
		mod390.setName(name.getValue());
		mod390.setFirstSurname(firstSurname.getValue());
		mod390.setSecondSurname(secondSurname.getValue());
		mod390.setContactPhone(phone.getValue());
		mod390.setReplacement(replacement.getValue());
		mod390.setReplacementDueInsolvencyState(replacementDueInsolvencyState.getValue());
		mod390.setReplacedReceipt(replacedReceipt.getValue());
		mod390.setInsolvencyDeclarations(insolvencyDeclarations.getValue());
		mod390.setInsolvencyStateThisYear(insolvencyStateThisYear.getValue());
		mod390.setAccrualRegime(accrualRegime.getValue());
		mod390.setAccrualRegimeTarget(accrualRegimeTarget.getValue());
		mod390.setTaxRefund(taxRefund.getValue());
		mod390.setSpecialGroupRegime(specialGroupRegime.getValue());
		mod390.setGroupNumber( groupNumber.getValue());
		mod390.setGroupDependent( groupDependent.getValue());
		mod390.setGroupRegimeType(groupRegimeType.getValue());
		mod390.setGroupDocument( groupDocument.getValue());
		mod390.setGroupDeclarations( groupDeclarations.getValue());
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

	@Override
	public void setCallback(IMod3902018CallBack callback) {
		this.callback = callback;
	}
	
	@Override
	public void refresh(Mod3902018 m390) {
		setValue(m390);
	}

}
