package com.esferalia.aon.gwt.fiscal.client.model;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDisplayTable;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.fiscal.FiscalModel;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class FiscalModelIdentificationData<T extends FiscalModel> 
	extends ScrollPanel implements HasValueChangeHandlers<T> {

	public FiscalModelIdentificationData(T model) {

		setStyleName(AON.CSS.aonScrollArea());
		AonDisplayTable tab = new AonDisplayTable();
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		setWidget(tab);
		
		AonDocumentTextBox document = new AonDocumentTextBox();
		
		InlineLabel nameLabel = new InlineLabel(AON.MSG.nameCompanyName());
		InlineLabel surnameLabel  = new InlineLabel(AON.MSG.surname());
		FlowPanel nameLabels = new FlowPanel();
		nameLabels.add(nameLabel);
		nameLabels.add(surnameLabel);
		
		AonTextBox name = new AonTextBox();
		name.setVisibleLength(25);
		name.setMaxLength(45);
		
		AonTextBox surname = new AonTextBox();
		surname.addStyleName(AON.CSS.aonMarginLeft());
		surname.setVisibleLength(25);
		surname.setMaxLength(45);
		
		FlowPanel nameBoxs = new FlowPanel();
		nameBoxs.add(name);
		nameBoxs.add(surname);

		tab.addRow()
			.addCell(new Label(AON.MSG.document()),AON.CSS.aonTableLabel())
			.addCell(document);
		tab.addRow()
			.addCell(nameLabels, AON.CSS.aonTableLabel())
			.addCell(nameBoxs);
		
		AonTextBox phone = new AonTextBox();
		phone.setVisibleLength(9);
		phone.setMaxLength(9);
		tab.addRow()
			.addCell(new Label(AON.MSG.phone()),AON.CSS.aonTableLabel())
			.addCell(phone);

		FlowPanel address1 = new FlowPanel();
		InlineLabel streetTypeLabel = new InlineLabel(AON.MSG.streetType());
		streetTypeLabel.setStyleName(AON.CSS.aonInnerLabel());
		address1.add(streetTypeLabel);
		AonTextBox streetInitial = new AonTextBox();
		streetInitial.setVisibleLength(3);
		streetInitial.setMaxLength(3);
		address1.add(streetInitial);
		InlineLabel streetNameLabel = new InlineLabel(AON.MSG.streetName());
		streetNameLabel.setStyleName(AON.CSS.aonInnerLabel());
		address1.add(streetNameLabel);
		AonTextBox streetName = new AonTextBox();
		streetName.setVisibleLength(30);
		streetName.setMaxLength(40);
		address1.add(streetName);
		tab.addRow()
			.addCell(new Label(),AON.CSS.aonTableLabel())
			.addCell(address1);
		
		FlowPanel address2 = new FlowPanel();
		InlineLabel streetNumberLabel = new InlineLabel(AON.MSG.streetNumber());
		streetNumberLabel.setStyleName(AON.CSS.aonInnerLabel());
		address2.add(streetTypeLabel);
		AonTextBox streetNumber = new AonTextBox();
		streetNumber.setVisibleLength(5);
		streetNumber.setMaxLength(5);
		address2.add(streetNumber);
		InlineLabel streetStairLabel = new InlineLabel(AON.MSG.streetStair());
		streetStairLabel.setStyleName(AON.CSS.aonInnerLabel());
		address2.add(streetStairLabel);
		AonTextBox streetStair = new AonTextBox();
		streetStair.setVisibleLength(2);
		streetStair.setMaxLength(2);
		address2.add(streetStair);
		InlineLabel streetFloorLabel = new InlineLabel(AON.MSG.streetFloor());
		streetFloorLabel.setStyleName(AON.CSS.aonInnerLabel());
		address2.add(streetFloorLabel);
		AonTextBox streetFloor = new AonTextBox();
		streetFloor.setVisibleLength(2);
		streetFloor.setMaxLength(2);
		address2.add(streetFloor);
		InlineLabel streetDoorLabel = new InlineLabel(AON.MSG.streetDoor());
		streetDoorLabel.setStyleName(AON.CSS.aonInnerLabel());
		address2.add(streetDoorLabel);
		AonTextBox streetDoor = new AonTextBox();
		streetDoor.setVisibleLength(2);
		streetDoor.setMaxLength(2);
		address2.add(streetDoor);
		tab.addRow()
			.addCell(new Label(),AON.CSS.aonTableLabel())
			.addCell(address2);

		AonTextBox town = new AonTextBox();
		town.setVisibleLength(35);
		town.setMaxLength(35);
		tab.addRow()
			.addCell(new Label(AON.MSG.town()),AON.CSS.aonTableLabel())
			.addCell(town);

		ProvinceListBox province = new ProvinceListBox();
		tab.addRow()
			.addCell(new Label(AON.MSG.province()),AON.CSS.aonTableLabel())
			.addCell(province);

		AonTextBox zip = new AonTextBox();
		zip.setVisibleLength(5);
		zip.setMaxLength(5);
		tab.addRow()
			.addCell(new Label(AON.MSG.zip()),AON.CSS.aonTableLabel())
			.addCell(zip);
		
		AonTextBox contactPerson = new AonTextBox();
		contactPerson.setVisibleLength(40);
		contactPerson.setMaxLength(40);
		tab.addRow()
			.addCell(new Label(AON.MSG.contactPerson()),AON.CSS.aonTableLabel())
			.addCell(contactPerson);
		
		AonTextBox contactPhone = new AonTextBox();
		contactPhone.setVisibleLength(9);
		contactPhone.setMaxLength(9);
		tab.addRow()
			.addCell(new Label(AON.MSG.contactPhone()),AON.CSS.aonTableLabel())
			.addCell(contactPhone);
		
		AonTextBox contactCellular = new AonTextBox();
		contactCellular.setVisibleLength(9);
		contactCellular.setMaxLength(9);
		tab.addRow()
			.addCell(new Label(AON.MSG.contactCellular()),AON.CSS.aonTableLabel())
			.addCell(contactCellular);
		
		AonTextBox contactMail = new AonTextBox();
		contactMail.setVisibleLength(40);
		contactMail.setMaxLength(40);
		tab.addRow()
			.addCell(new Label(AON.MSG.contactCellular()),AON.CSS.aonTableLabel())
			.addCell(contactMail);
		

		document.addValueChangeHandler(event -> {
			model.setDocument(document.getValue());
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
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});
		
		name.addValueChangeHandler(event -> {		
			model.setName(name.getValue());
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});
		
		surname.addValueChangeHandler(event -> {		
			model.setSurname(surname.getValue());
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});

		phone.addValueChangeHandler(event -> {
			model.setPhone(phone.getValue());
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});
		
		contactPerson.addValueChangeHandler(event -> {
			model.setContactPerson(contactPerson.getValue());
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});
		
		contactPhone.addValueChangeHandler(event -> {
			model.setContactPhone(contactPhone.getValue());
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});
		
		contactCellular.addValueChangeHandler(event -> {
			model.setContactCellular(contactCellular.getValue());
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});
		
		contactMail.addValueChangeHandler(event -> {
			model.setContactEmail(contactMail.getValue());
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});
		
		streetInitial.addValueChangeHandler(event -> {
			model.setStreetInitial(streetInitial.getValue());
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});
		
		streetName.addValueChangeHandler(event -> {
			model.setStreetName(streetName.getValue());
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});
		
		streetNumber.addValueChangeHandler(event -> {
			model.setStreetNumber(streetNumber.getValue());
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});
		
		streetStair.addValueChangeHandler(event -> {
			model.setStreetStair(streetStair.getValue());
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});
		
		streetFloor.addValueChangeHandler(event -> {
			model.setStreetFloor(streetFloor.getValue());
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});
		
		streetDoor.addValueChangeHandler(event -> {
			model.setStreetDoor(streetDoor.getValue());
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});
		
		town.addValueChangeHandler(event -> {
			model.setTown(town.getValue());
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});
		
		province.addChangeHandler(event -> {
			model.setProvince(Province.values()[province.getSelectedIndex()].getName());
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});
		
		zip.addValueChangeHandler(event -> {
			model.setZip(zip.getValue());
			ValueChangeEvent.fire(FiscalModelIdentificationData.this, model);
		});

		document.setValue(model.getDocument());
		document.setEnabled(!model.isFinished());

		name.setValue(model.getName());
		name.setEnabled(!model.isFinished());
		
		surname.setValue(model.getSurname());
		surname.setEnabled(!model.isFinished());
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
		
		phone.setValue(model.getPhone());
		phone.setEnabled(!model.isFinished());
		
		contactPerson.setValue(model.getContactPerson());
		contactPerson.setEnabled(!model.isFinished());
		
		contactPhone.setValue(model.getContactPhone());
		contactPhone.setEnabled(!model.isFinished());
		
		contactCellular.setValue(model.getContactCellular());
		contactCellular.setEnabled(!model.isFinished());
		
		contactMail.setValue(model.getContactEmail());
		contactMail.setEnabled(!model.isFinished());
		
		streetInitial.setValue(model.getStreetInitial());
		streetInitial.setEnabled(!model.isFinished());
		
		streetName.setValue(model.getStreetName());
		streetName.setEnabled(!model.isFinished());
		
		streetNumber.setValue(model.getStreetNumber());
		streetNumber.setEnabled(!model.isFinished());
		
		streetStair.setValue(model.getStreetStair());
		streetStair.setEnabled(!model.isFinished());
		
		streetFloor.setValue(model.getStreetFloor());
		streetFloor.setEnabled(!model.isFinished());
		
		streetDoor.setValue(model.getStreetDoor());
		streetDoor.setEnabled(!model.isFinished());
		
		town.setValue(model.getTown());
		town.setEnabled(!model.isFinished());
		
		province.setSelectedIndex( Province.getByName(model.getProvince()).ordinal());
		province.setEnabled(!model.isFinished());
		
		zip.setValue(model.getZip());
		zip.setEnabled(!model.isFinished());
	}

	@Override
	public HandlerRegistration addValueChangeHandler(ValueChangeHandler<T> handler) {
		return super.addHandler(handler, ValueChangeEvent.getType());
	}

	
}
