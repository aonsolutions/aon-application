package com.esferalia.aon.gwt.fiscal.client.mod390.e2024;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod390.e2024.Model3902024.Model3902024Callback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;

class Page00 extends PageAbs {

	private AonDocumentTextBox document = new AonDocumentTextBox(); 
	private AonTextBox name = new AonTextBox();
	private AonTextBox firstSurname = new AonTextBox();
	private AonTextBox secondSurname = new AonTextBox();
	private AonTextBox phone = new AonTextBox();
	private CheckBox replacement = new CheckBox( AON.MSG.replacement());
	private AonTextBox replacedReceipt = new AonTextBox();
	private CheckBox replacementDueInsolvencyState = new CheckBox( AON.MSG.replacementDueInsolvencyState());
	private CheckBox taxRefund = new CheckBox( AON.MSG.taxRefund());
	private CheckBox specialGroupRegime = new CheckBox( AON.MSG.specialGroupRegime());
	private AonTextBox groupNumber = new AonTextBox();
	private CheckBox groupDependent = new CheckBox( AON.MSG.groupDependent());
	private CheckBox groupDeclarations = new CheckBox( AON.MSG.groupDeclarations());
	private CheckBox insolvencyDeclarations = new CheckBox( AON.MSG.insolvencyDeclarations());
	private CheckBox groupRegimeType = new CheckBox( AON.MSG.groupRegimeType());
	private AonDocumentTextBox groupDocument = new AonDocumentTextBox();
	private CheckBox insolvencyStateThisYear = new CheckBox( AON.MSG.insolvencyStateThisYear());
	private CheckBox insolvencyStateLastPeriod = new CheckBox( AON.MSG.insolvencyStateLastPeriod());
	private CheckBox accrualRegime = new CheckBox( AON.MSG.accrualRegime());
	private CheckBox accrualRegimeTarget = new CheckBox( AON.MSG.accrualRegimeTarget());

	public Page00(Model3902024Callback callback) {
		super(callback);
		paint();
		setValue();
	}

	protected void paint() {
		ScrollPanel scroll = new ScrollPanel();
		FlowPanel basePanel = new FlowPanel();
		scroll.add(basePanel);
		setWidget(scroll);
		
		basePanel.add(getTitle(AON.MSG.deponentData()));
		basePanel.add(getSubtitle(AON.MSG.identification()));
		
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab);
		
		document.setEnabled(false);
		
		name.setVisibleLength(25);
		firstSurname.setMaxLength(15);
		firstSurname.setVisibleLength(16); 
		secondSurname.setMaxLength(15);
		secondSurname.setVisibleLength(16); 
		FlowPanel surnames = new FlowPanel(); 
		surnames.add(firstSurname);
		surnames.add(secondSurname);
		
		replacedReceipt.setVisibleLength(13);
		
		FlowPanel groupPanel1 = new FlowPanel();
		InlineLabel gpl1 = new InlineLabel(AON.MSG.groupNumber());
		gpl1.setStyleName(AON.CSS.aonInnerLabel());
		groupPanel1.add(gpl1);
		groupNumber.setVisibleLength(5);
		groupNumber.setMaxLength(5);
		groupPanel1.add(groupNumber);
		groupPanel1.add(groupDependent);
		
		FlowPanel groupPanel2 = new FlowPanel();
		groupPanel2.add(groupRegimeType);
		InlineLabel gpl2 = new InlineLabel(AON.MSG.groupRegimeType());
		gpl2.setStyleName(AON.CSS.aonInnerLabel());
		groupPanel2.add(gpl2);
		groupPanel2.add(groupDocument);
		
		tab
			.addLabelWidgetRow(AON.MSG.document(), document)
			.addLabelWidgetRow(AON.MSG.name(), name)
			.addLabelWidgetRow(AON.MSG.surname(), surnames)
			.addLabelWidgetRow(AON.MSG.phone(), phone)
			.addLabelWidgetRow("", replacement)
			.addLabelWidgetRow("", replacementDueInsolvencyState)
			.addLabelWidgetRow(AON.MSG.replacedReceipt(), replacedReceipt)
			.addLabelWidgetRow("", taxRefund)
			.addLabelWidgetRow("", specialGroupRegime)
			.addLabelWidgetRow("", groupPanel1)
			.addLabelWidgetRow("", groupPanel2)
			.addLabelWidgetRow("", groupDeclarations)
			;
		
		basePanel.add(getSubtitle(AON.MSG.insolvencyState()));
		
		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab1);
		
		tab1.addRow().addCell(insolvencyStateThisYear);
		tab1.addRow().addCell(insolvencyStateLastPeriod);
		
		basePanel.add(getSubtitle(AON.MSG.cashAccrualRegime()));
		
		AonDisplayTable tab2 = new AonDisplayTable();
		tab2.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab2.addStyleName(AON.CSS.aonBlockCenter());
		basePanel.add(tab2);
		
		tab2.addRow().addCell(accrualRegime);
		tab2.addRow().addCell(accrualRegimeTarget);
		
		name.addValueChangeHandler(event ->{
			getModel().setName(name.getValue());
			markAsDirty();
		});
		
		firstSurname.addValueChangeHandler(event ->{
			getModel().setFirstSurname(firstSurname.getValue());
			markAsDirty();
		});
		secondSurname.addValueChangeHandler(event ->{
			getModel().setSecondSurname(secondSurname.getValue());
			markAsDirty();
		});
		phone.addValueChangeHandler(event ->{
			getModel().setContactPhone(phone.getValue());
			markAsDirty();
		});
		replacement.addClickHandler(event ->{
			getModel().setReplacement(replacement.getValue());
			markAsDirty();
		});
		replacementDueInsolvencyState.addClickHandler(event ->{
			getModel().setReplacementDueInsolvencyState(replacementDueInsolvencyState.getValue());
			markAsDirty();
		});
		replacedReceipt.addValueChangeHandler(event ->{
			getModel().setReplacedReceipt(replacedReceipt.getValue());
			markAsDirty();
		});
		insolvencyDeclarations.addClickHandler(event ->{
			getModel().setInsolvencyDeclarations(insolvencyDeclarations.getValue());
			markAsDirty();
		});
		insolvencyStateThisYear.addClickHandler(event ->{
			getModel().setInsolvencyStateThisYear(insolvencyStateThisYear.getValue());
			markAsDirty();
		});
		insolvencyStateLastPeriod.addClickHandler(event ->{
			getModel().setInsolvencyStateLastPeriod(insolvencyStateLastPeriod.getValue());
			markAsDirty();
		});
		accrualRegime.addClickHandler(event ->{
			getModel().setAccrualRegime(accrualRegime.getValue());
			markAsDirty();
		});
		accrualRegimeTarget.addClickHandler(event ->{
			getModel().setAccrualRegimeTarget(accrualRegimeTarget.getValue());
			markAsDirty();
		});
		taxRefund.addClickHandler(event ->{
			getModel().setTaxRefund(taxRefund.getValue());
			markAsDirty();
		});
		
		specialGroupRegime.addClickHandler(event ->{
			getModel().setSpecialGroupRegime(specialGroupRegime.getValue());
			onClickSpecialGroupRegime();
			markAsDirty();
		});
		groupNumber.addValueChangeHandler(event ->{
			getModel().setGroupNumber( groupNumber.getValue());
			markAsDirty();
		});
		groupDependent.addClickHandler(event ->{
			getModel().setGroupDependent( groupDependent.getValue());
			markAsDirty();
		});
		groupRegimeType.addClickHandler(event ->{
			getModel().setGroupRegimeType(groupRegimeType.getValue());
			markAsDirty();
		});
		groupDocument.addValueChangeHandler(event ->{
			getModel().setGroupDocument( groupDocument.getValue());
			markAsDirty();
		});
		groupDeclarations.addValueChangeHandler(event ->{
			getModel().setGroupDeclarations( groupDeclarations.getValue());
			markAsDirty();
		});
		
	}

	@Override
	protected void setValue() {
		document.setValue(getModel().getDocument(),false);
		name.setValue(getModel().getName(),false);
		replacement.setValue(getModel().isReplacement(),false);
		replacedReceipt.setValue(getModel().getReplacedReceipt(),false);
		replacementDueInsolvencyState.setValue(getModel().isReplacementDueInsolvencyState(),false);
		if (getModel().isLegalEntity()) {
			name.setMaxLength(37);
			firstSurname.setValue(null,false);
			secondSurname.setValue(null,false);
			firstSurname.setEnabled(false);
			secondSurname.setEnabled(false);
		} else {
			name.setMaxLength(15);
			firstSurname.setValue(getModel().getFirstSurname(),false);
			secondSurname.setValue(getModel().getSecondSurname(),false);
			firstSurname.setEnabled(true);
			secondSurname.setEnabled(true);
		}
		phone.setValue(getModel().getContactPhone(),false);
		taxRefund.setValue(getModel().isTaxRefund(),false);
		groupNumber.setValue(getModel().getGroupNumber(),false);
		groupDependent.setValue(getModel().isGroupDependent(),false);
		groupDeclarations.setValue(getModel().isGroupDeclarations(),false);
		
		insolvencyStateThisYear.setValue(getModel().isInsolvencyStateThisYear(),false);
		insolvencyStateLastPeriod.setValue(getModel().isInsolvencyStateLastPeriod(),false);
		accrualRegime.setValue(getModel().isAccrualRegime(),false);
		accrualRegimeTarget.setValue(getModel().isAccrualRegimeTarget(),false);
		groupRegimeType.setValue(getModel().isGroupRegimeType(),false);
		groupDocument.setValue(getModel().getGroupDocument(),false);
		groupDeclarations.setValue(getModel().isGroupDeclarations(),false);
		specialGroupRegime.setValue(getModel().isSpecialGroupRegime(),false);
		onClickSpecialGroupRegime();
	}

	private void onClickSpecialGroupRegime() {
		groupNumber.setEnabled(specialGroupRegime.getValue());
		groupDependent.setEnabled(specialGroupRegime.getValue());
		groupRegimeType.setEnabled(specialGroupRegime.getValue());
		groupDocument.setEnabled(specialGroupRegime.getValue());
		groupDeclarations.setEnabled(specialGroupRegime.getValue());
		
		if (!specialGroupRegime.getValue().booleanValue()) {
			groupNumber.setValue(null, false);
			getModel().setGroupNumber( groupNumber.getValue());
			groupDependent.setValue(false, false);
			getModel().setGroupDependent( groupDependent.getValue());
			groupRegimeType.setValue(false, false);
			getModel().setGroupRegimeType(groupRegimeType.getValue());
			groupDocument.setValue(null, false);
			getModel().setGroupDocument( groupDocument.getValue());
			groupDeclarations.setValue(false, false);
			getModel().setGroupDeclarations( groupDeclarations.getValue());
		}
	}

}
