package com.esferalia.aon.gwt.fiscal.client.mod200.e2017;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog;
import com.esferalia.aon.gwt.common.client.widget.ConfirmDialog.ConfirmDialogCallback;
import com.esferalia.aon.gwt.common.client.widget.CustomDialog;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceCountryListBox;
import com.esferalia.aon.occam.api.model.CompanyParticipation;
import com.esferalia.aon.occam.api.model.type.Country;
import com.esferalia.aon.occam.api.model.type.Province;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ParticipationPanel extends CustomDialog {
	
	public static interface ParticipationPanelCallback {
		void onAccept(int index, CompanyParticipation cp);
		void onCancel();
		default void onClose() {
			this.onCancel();
		}
	}

	interface ParticipationPanelBinder extends UiBinder<Widget, ParticipationPanel> {
	}
	private static final ParticipationPanelBinder participationPanelBinder = GWT
			.create(ParticipationPanelBinder.class);

	@UiField
	Button acceptButton;
	@UiField
	Button cancelButton;
	
	@UiField
	TextBox document;
	@UiField
	TextBox  name;
	@UiField
	ProvinceCountryListBox  province;
	
	@UiField
	DoubleBox percent;
	@UiField
	DoubleBox nominalValue;
	@UiField
	DoubleBox bookValue;
	@UiField
	DoubleBox incomes;
	
	@UiField
	DoubleBox aValue;
	@UiField
	DoubleBox bValue;
	@UiField
	DoubleBox cValue;
	@UiField
	DoubleBox dValue;
	@UiField
	DoubleBox eValue;
	
	@UiField
	DoubleBox capital;
	@UiField
	DoubleBox reserve;
	@UiField
	DoubleBox otherAmounts;
	@UiField
	DoubleBox result;

	private ParticipationPanelCallback callback;
	private int index;

	public ParticipationPanel( ParticipationPanelCallback callback) {
		this.callback = callback;
		setVisible(false);
		setAnimationEnabled(true);
		setGlassEnabled(true);
		setModal(true);
		setCaption(AON.MSG.participationsOut());
		Widget ui = participationPanelBinder.createAndBindUi(this);
		setWidget(ui);
	}

	public void dump(int index, CompanyParticipation companyParticipation) {
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
		this.dValue.setValue(companyParticipation.getcValue());
		this.eValue.setValue(companyParticipation.getdValue());
		this.capital.setValue(companyParticipation.getCapital());
		this.reserve.setValue(companyParticipation.getReserve());
		this.otherAmounts.setValue(companyParticipation.getOtherAmounts());
		this.result.setValue(companyParticipation.getResult());
	}

	@UiHandler("acceptButton")
	void onAcceptButtonClick(ClickEvent event) {
		onAccept();
	}
	@UiHandler("cancelButton")
	void onCancelButtonClick(ClickEvent event) {
		onCancel();
	}
	
	@Override
	public void onClose() {
		onCancel();	
	}
	
	public void onCancel() {
		ConfirmDialog cd = new ConfirmDialog();
		cd.confirm(AON.MSG.cancelAction(), new ConfirmDialogCallback() {
			
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
		CompanyParticipation companyParticipation = new CompanyParticipation();
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
		companyParticipation.setcValue(this.dValue.getValue());
		companyParticipation.setdValue(this.eValue.getValue());
		companyParticipation.setCapital(this.capital.getValue());
		companyParticipation.setReserve(this.reserve.getValue());
		companyParticipation.setOtherAmounts(this.otherAmounts.getValue());
		companyParticipation.setResult(this.result.getValue());
		callback.onAccept(index,companyParticipation);
		this.hide();
	}

}
