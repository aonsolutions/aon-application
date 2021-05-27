package com.esferalia.aon.gwt.fiscal.client.model;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class FiscalModelIdentificationData<FM extends FiscalModel> extends ResizeComposite implements RequiresResize {

	@SuppressWarnings("rawtypes")
	interface FiscalModelIdentificationDataBinder extends UiBinder<Widget, FiscalModelIdentificationData> {}
	
	private static final FiscalModelIdentificationDataBinder DATA_BINDER = GWT
			.create(FiscalModelIdentificationDataBinder.class);
	
	@UiField
	DocumentTextBox document;
	@UiField
	Label nameLabel;
	@UiField
	TextBox name;
	@UiField
	Label surnameLabel;
	@UiField
	TextBox surname;
	@UiField
	TextBox phone;
	
	@UiField
	TextBox contactPerson;
	@UiField
	TextBox contactPhone;
	@UiField
	TextBox contactCellular;
	@UiField
	TextBox contactMail;
	
	@UiField
	TextBox streetInitial;
	@UiField
	TextBox streetName;
	@UiField
	TextBox streetNumber;
	@UiField
	TextBox streetStair;
	@UiField
	TextBox streetFloor;
	@UiField
	TextBox streetDoor;
	@UiField
	TextBox town;
	@UiField
	ProvinceListBox province;
	@UiField
	TextBox zip;
	
	private IFiscalModelCallback<FM> callback;
	
	public FiscalModelIdentificationData(IFiscalModelCallback<FM> callback) {
		this.callback = callback;
		
		Widget ui = DATA_BINDER.createAndBindUi(this);
		initWidget(ui);
		
		document.setValue(callback.getFiscalModel().getDocument());
		document.setEnabled(!callback.isFinished());

		name.setValue(callback.getFiscalModel().getName());
		name.setEnabled(!callback.isFinished());
		surname.setValue(callback.getFiscalModel().getSurname());
		surname.setEnabled(!callback.isFinished());
		changeNameFields();
		
		phone.setValue(callback.getFiscalModel().getPhone());
		phone.setEnabled(!callback.isFinished());
		contactPerson.setValue(callback.getFiscalModel().getContactPerson());
		contactPerson.setEnabled(!callback.isFinished());
		contactPhone.setValue(callback.getFiscalModel().getContactPhone());
		contactPhone.setEnabled(!callback.isFinished());
		contactCellular.setValue(callback.getFiscalModel().getContactCellular());
		contactCellular.setEnabled(!callback.isFinished());
		contactMail.setValue(callback.getFiscalModel().getContactEmail());
		contactMail.setEnabled(!callback.isFinished());
		streetInitial.setValue(callback.getFiscalModel().getStreetInitial());
		streetInitial.setEnabled(!callback.isFinished());
		streetName.setValue(callback.getFiscalModel().getStreetName());
		streetName.setEnabled(!callback.isFinished());
		streetNumber.setValue(callback.getFiscalModel().getStreetNumber());
		streetNumber.setEnabled(!callback.isFinished());
		streetStair.setValue(callback.getFiscalModel().getStreetStair());
		streetStair.setEnabled(!callback.isFinished());
		streetFloor.setValue(callback.getFiscalModel().getStreetFloor());
		streetFloor.setEnabled(!callback.isFinished());
		streetDoor.setValue(callback.getFiscalModel().getStreetDoor());
		streetDoor.setEnabled(!callback.isFinished());
		town.setValue(callback.getFiscalModel().getTown());
		town.setEnabled(!callback.isFinished());
		province.setSelectedIndex( Province.getByName(callback.getFiscalModel().getProvince()).ordinal());
		province.setEnabled(!callback.isFinished());
		zip.setValue(callback.getFiscalModel().getZip());
		zip.setEnabled(!callback.isFinished());
	}
	
	private void changeNameFields() {
		if (AonDocumentUtil.isEntity( document.getValue() )) {
			nameLabel.setText(AON.MSG.nameCompanyName());
			surnameLabel.setVisible(false);
			surname.setValue(null);
			surname.setVisible(false);
		} else {
			nameLabel.setText(AON.MSG.name());
			surnameLabel.setVisible(true);
			surname.setVisible(true);
		}
	}

	@UiHandler("document")
	void onDocumentChanged(ChangeEvent  event) {
		callback.getFiscalModel().setDocument(document.getValue());
		changeNameFields();
		callback.identificationLabelChanged();
		callback.markAsDirty();
	}
	
	@UiHandler("name")
	void onNameChanged(ChangeEvent  event) {
		callback.getFiscalModel().setName(name.getValue());
		callback.identificationLabelChanged();
		callback.markAsDirty();
	}
	
	@UiHandler("surname")
	void onSurnameChanged(ChangeEvent  event) {
		callback.getFiscalModel().setSurname(surname.getValue());
		callback.identificationLabelChanged();
		callback.markAsDirty();
	}
	
	@UiHandler("phone")
	void onPhoneChanged(ChangeEvent  event) {
		callback.getFiscalModel().setPhone(phone.getValue());
		callback.markAsDirty();
	}
	
	@UiHandler("contactPerson")
	void onContactPersonChanged(ChangeEvent  event) {
		callback.getFiscalModel().setContactPerson(contactPerson.getValue());
		callback.markAsDirty();
	}
	
	@UiHandler("contactPhone")
	void onContactPhoneChanged(ChangeEvent  event) {
		callback.getFiscalModel().setContactPhone(contactPhone.getValue());
		callback.markAsDirty();
	}
	
	@UiHandler("contactCellular")
	void onContactCellularChanged(ChangeEvent  event) {
		callback.getFiscalModel().setContactCellular(contactCellular.getValue());
		callback.markAsDirty();
	}
	
	@UiHandler("contactMail")
	void onContactMailChanged(ChangeEvent  event) {
		callback.getFiscalModel().setContactEmail(contactMail.getValue());
		callback.markAsDirty();
	}
	
	@UiHandler("streetInitial")
	void onStreetInitialChanged(ChangeEvent  event) {
		callback.getFiscalModel().setStreetInitial(streetInitial.getValue());
		callback.markAsDirty();
	}
	
	@UiHandler("streetName")
	void onStreetNameChanged(ChangeEvent  event) {
		callback.getFiscalModel().setStreetName(streetName.getValue());
		callback.markAsDirty();
	}
	
	@UiHandler("streetNumber")
	void onStreetNumberChanged(ChangeEvent  event) {
		callback.getFiscalModel().setStreetNumber(streetNumber.getValue());
		callback.markAsDirty();
	}
	
	@UiHandler("streetStair")
	void onStreetStairChanged(ChangeEvent  event) {
		callback.getFiscalModel().setStreetStair(streetStair.getValue());
		callback.markAsDirty();
	}
	
	@UiHandler("streetFloor")
	void onStreetFloorChanged(ChangeEvent  event) {
		callback.getFiscalModel().setStreetFloor(streetFloor.getValue());
		callback.markAsDirty();
	}
	
	@UiHandler("streetDoor")
	void onStreetDoorChanged(ChangeEvent  event) {
		callback.getFiscalModel().setStreetDoor(streetDoor.getValue());
		callback.markAsDirty();
	}
	
	@UiHandler("town")
	void onTownChanged(ChangeEvent  event) {
		callback.getFiscalModel().setTown(town.getValue());
		callback.markAsDirty();
	}
	
	@UiHandler("province")
	void onProvinceChanged(ChangeEvent  event) {
		callback.getFiscalModel().setProvince(Province.values()[province.getSelectedIndex()].getName());
		callback.markAsDirty();
	}
	
	@UiHandler("zip")
	void onZipChanged(ChangeEvent  event) {
		callback.getFiscalModel().setZip(zip.getValue());
		callback.markAsDirty();
	}
	
	
}
