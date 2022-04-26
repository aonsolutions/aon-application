package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ProvinceCountryListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonConfirmDialog.AonConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonCustomDialog;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.occam.mod200.api.model.Mod200CompanyParticipation;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ParticipationPanel extends AonCustomDialog {
	
	public static interface ParticipationPanelCallback {
		void onAccept(int index, Mod200CompanyParticipation cp);
		void onCancel();
		default void onClose() {
			this.onCancel();
		}
	}
	
	private AonDocumentTextBox document = new AonDocumentTextBox();
	private AonTextBox name = new AonTextBox();
	private ProvinceCountryListBox province = new ProvinceCountryListBox();
	private AonDoubleBox percent = new AonDoubleBox();
	private AonDoubleBox nominalValue = new AonDoubleBox();
	private AonDoubleBox bookValue = new AonDoubleBox();
	private AonDoubleBox incomes = new AonDoubleBox();
	private AonDoubleBox aValue = new AonDoubleBox();
	private AonDoubleBox bValue = new AonDoubleBox();
	private AonDoubleBox cValue = new AonDoubleBox();
	private AonDoubleBox dValue = new AonDoubleBox();
	private AonDoubleBox eValue = new AonDoubleBox();
	private AonDoubleBox fValue = new AonDoubleBox();
	private AonDoubleBox gValue = new AonDoubleBox();
	private AonDoubleBox capital = new AonDoubleBox();
	private AonDoubleBox reserve = new AonDoubleBox();
	private AonDoubleBox otherAmounts = new AonDoubleBox();
	private AonDoubleBox result = new AonDoubleBox();

	private ParticipationPanelCallback callback;
	private int index;

	public ParticipationPanel( ParticipationPanelCallback callback) {
		this.callback = callback;
		setVisible(false);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setCaption(AON.MSG.participationsOut());
		paint();
	}

	public void dump(int index, Mod200CompanyParticipation companyParticipation) {
		this.index = index;
		this.document.setValue(companyParticipation.getDocument());
		this.name.setValue(companyParticipation.getName());
		int idx = companyParticipation.getProvince();
		if (idx > 0 && idx < Province.values().length) {
			this.province.setSelectedIndex(idx);
		} else {
			Country c = Country.safeValueOf(companyParticipation.getCountry());
			if (c != null) {
				this.province.setSelectedIndex(c.ordinal() + Province.values().length);
			}
		}
		this.percent.setValue(companyParticipation.getPercent());
		this.nominalValue.setValue(companyParticipation.getNominalValue());
		this.bookValue.setValue(companyParticipation.getBookValue());
		this.incomes.setValue(companyParticipation.getIncomes());
		this.aValue.setValue(companyParticipation.getaValue());
		this.bValue.setValue(companyParticipation.getbValue());
		this.cValue.setValue(companyParticipation.getccValue());
		this.dValue.setValue(companyParticipation.getddValue());
		this.eValue.setValue(companyParticipation.geteValue());
		this.fValue.setValue(companyParticipation.getcValue());
		this.gValue.setValue(companyParticipation.getdValue());
		this.capital.setValue(companyParticipation.getCapital());
		this.reserve.setValue(companyParticipation.getReserve());
		this.otherAmounts.setValue(companyParticipation.getOtherAmounts());
		this.result.setValue(companyParticipation.getResult());
	}

	@Override
	public void onClose() {
		onCancel();	
	}
	
	public void onCancel() {
		AonConfirmDialog cd = new AonConfirmDialog();
		cd.confirm(AON.MSG.cancelAction(), new AonConfirmDialogCallback() {
			
			@Override
			public void onCancel() {}
			
			@Override
			public void onAccept() {
				callback.onCancel();
				hide();
			}
		});
	}
	
	public void onAccept() {
		Mod200CompanyParticipation companyParticipation = new Mod200CompanyParticipation();
		companyParticipation.setDocument(this.document.getValue());
		companyParticipation.setName(this.name.getValue());
		if (this.province.getSelectedIndex() < Province.values().length) {
			companyParticipation.setProvince(this.province.getSelectedIndex());
		} else {
			Country c = Country.values()[this.province.getSelectedIndex() - Province.values().length];
			companyParticipation.setCountry(c.getIso2());
		}
		companyParticipation.setPercent(this.percent.getValue());
		companyParticipation.setNominalValue(this.nominalValue.getValue());
		companyParticipation.setBookValue(this.bookValue.getValue());
		companyParticipation.setIncomes(this.incomes.getValue());
		companyParticipation.setaValue(this.aValue.getValue());
		companyParticipation.setbValue(this.bValue.getValue());
		companyParticipation.setccValue(this.cValue.getValue());
		companyParticipation.setcValue(this.fValue.getValue());
		companyParticipation.setdValue(this.gValue.getValue());
		companyParticipation.setddValue(this.dValue.getValue());
		companyParticipation.seteValue(this.eValue.getValue());
		companyParticipation.setCapital(this.capital.getValue());
		companyParticipation.setReserve(this.reserve.getValue());
		companyParticipation.setOtherAmounts(this.otherAmounts.getValue());
		companyParticipation.setResult(this.result.getValue());
		callback.onAccept(index,companyParticipation);
		this.hide();
	}
	
	private void paint() {
		
		FlowPanel rootPanel = new FlowPanel();
		
		Label label = new Label(AON.MSG.partMsg1_2018());
		label.setStyleName(AON.CSS.aonMargin());
		label.addStyleName(AON.CSS.aonBold());
		label.addStyleName(AON.CSS.aonWidthAlmostAll());
		label.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(label);
		
		// Datos de la participada
		
		rootPanel.add(getSubtitle(AON.MSG.partMsg2()));
		
		AonDisplayTable tab1 = new AonDisplayTable();
		tab1.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab1.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab1);

		document.setVisibleLength(9);
		name.setVisibleLength(40);
		name.setMaxLength(30);
		
		addRow(tab1, AON.MSG.nif(), document);
		addRow(tab1, AON.MSG.companyName(), name);
		addRow(tab1, AON.MSG.province() + "/" + AON.MSG.country(), province);
		
		// Datos en los registros de la declarante
		
		rootPanel.add(getSubtitle(AON.MSG.partMsg3()));
		
		AonDisplayTable tab2 = new AonDisplayTable();
		tab2.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab2.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab2);
		
		addRow(tab2, AON.MSG.partMsg4(), percent);
		addRow(tab2, AON.MSG.partMsg5(), nominalValue);
		addRow(tab2, AON.MSG.partMsg6(), bookValue);
		addRow(tab2, AON.MSG.partMsg7(), incomes);
		
		// Correcciones valorativas por deterioro y cambio de valor razonable
		
		rootPanel.add(getSubtitle(AON.MSG.partMsg8()));
		
		AonDisplayTable tab3 = new AonDisplayTable();
		tab3.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab3.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab3);
		
		addRow(tab3, AON.MSG.partMsg9(), aValue);
		addRow(tab3, AON.MSG.partMsg101(), bValue);
		addRow(tab3, AON.MSG.partMsg111(), cValue);
		addRow(tab3, AON.MSG.partMsg123(), dValue);
		addRow(tab3, AON.MSG.partMsg124(), eValue);
		addRow(tab3, AON.MSG.partMsg125(), fValue);
		addRow(tab3, AON.MSG.partMsg126(), gValue);
		
		// Datos adicionales de la participada
		
		rootPanel.add(getSubtitle(AON.MSG.partMsg13()));
		
		AonDisplayTable tab4 = new AonDisplayTable();
		tab4.addStyleName(AON.CSS.aonWidthAlmostAll());
		tab4.addStyleName(AON.CSS.aonBlockCenter());
		rootPanel.add(tab4);
		
		addRow(tab4, AON.MSG.partMsg14(), capital);
		addRow(tab4, AON.MSG.partMsg15(), reserve);
		addRow(tab4, AON.MSG.partMsg16(), otherAmounts);
		addRow(tab4, AON.MSG.partMsg17(), result);
		
		// Botones Aceptar y Cancelar
		
		FlowPanel buttonsPanel = new FlowPanel();
		buttonsPanel.setStyleName(AON.CSS.aonPadding());
		buttonsPanel.addStyleName(AON.CSS.aonMarginTop());
		buttonsPanel.addStyleName(AON.CSS.aonTextCenter());

		Button acceptButton = new Button();
		acceptButton.setStyleName(AON.CSS.aonOkButton());
		acceptButton.setText(AON.MSG.accept());
		acceptButton.addClickHandler(event -> {
			onAccept();
		});
		
		Button cancelButton = new Button();
    	cancelButton.setStyleName(AON.CSS.aonCancelButton());
    	cancelButton.addStyleName(AON.CSS.aonMarginLeft());
    	cancelButton.setText( AON.MSG.cancelAction());
		cancelButton.addClickHandler(event -> {
			onCancel();
		});
		
		buttonsPanel.add(acceptButton);
		buttonsPanel.add(cancelButton);
		rootPanel.add(buttonsPanel);
		
		add(rootPanel);
		
	}
	
	private Label getSubtitle(String text) {
		Label subtitle = new Label(text);
		subtitle.setStyleName(AON.CSS.aonMarginTop());
		subtitle.addStyleName(AON.CSS.aonBold());
		subtitle.addStyleName(AON.CSS.aonWidthAlmostAll());
		subtitle.addStyleName(AON.CSS.aonBlockCenter());
		subtitle.addStyleName(AON.CSS.aonBorderBottom());
		return subtitle;
	}
	
	public void addRow(AonDisplayTable tab, String label, Widget widget) {
		tab.addRow()
			.addCell(new Label(label), AON.CSS.aonWidth600(), AON.CSS.aonBorderBottom())
			.addCell(widget);
	}

}
