package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;

public class Model303IdentificationData extends ScrollPanel {

	public interface IModel303IdentificationDataCallback {
		String getDocument();
		void documentChanged(String value);
		
		String getName();
		void nameChanged(String value);

		String getSurname();
		void surnameChanged(String value);
		
		String getPhone();
		void phoneChanged(String value);

		String getStreetInitial();
		void streetInitialChanged(String value);

		String getStreetName();
		void streetNameChanged(String value);

		String getStreetNumber();
		void streetNumberChanged(String value);
		
		String getStreetStair();
		void streetStairChanged(String value);

		String getStreetFloor();
		void streetFloorChanged(String value);
		
		String getStreetDoor();
		void streetDoorChanged(String value);

		String getTown();
		void townChanged(String value);
		
		String getProvince();
		void provinceChanged(String value);

		String getZip();
		void zipChanged(String value);
		
		String getContactPerson();
		void contactPersonChanged(String value);
		
		String getContactPhone();
		void contactPhoneChanged(String value);

		String getContactCellular();
		void contactCellularChanged(String value);
		
		String getContactEmail();
		void contactMailChanged(String value);
		
		boolean isFinished();
	}
	
	public Model303IdentificationData(IModel303IdentificationDataCallback callback) {
		setStyleName(AON.CSS.aonScrollArea());
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.CSS.aonTable());
		tab.addStyleName(AON.CSS.aonMarginTop());
		tab.addStyleName(AON.CSS.aonBlockCenter());
		
		tab.getColumnFormatter().setWidth(0, "300px");
		tab.getColumnFormatter().setWidth(1, "auto");
		
		InlineLabel nameLabel = new InlineLabel(AON.MSG.nameCompanyName());
		InlineLabel surnameLabel  = new InlineLabel(AON.MSG.surname());
		FlowPanel nameLabels = new FlowPanel();
		nameLabels.add(nameLabel);
		nameLabels.add(surnameLabel);
		
		Widget[] labels = new Widget[]{
				new InlineLabel(AON.MSG.document()),
				nameLabels,
				new InlineLabel(AON.MSG.phone()),
				new InlineLabel(AON.MSG.address()),
				new InlineLabel(AON.MSG.town()),
				new InlineLabel(AON.MSG.province()),
				new InlineLabel(AON.MSG.zip()),
				new InlineLabel(AON.MSG.contactPerson()),
				new InlineLabel(AON.MSG.contactPhone()),
				new InlineLabel(AON.MSG.contactCellular()),
				new InlineLabel(AON.MSG.contactMail()),
		};
		for (int i = 0; i < labels.length; i++) {
			tab.setWidget( i, 0, labels[i]);
			tab.getCellFormatter().setStyleName( i ,0, AON.CSS.aonTableLabel());
			tab.getCellFormatter().addStyleName( i ,0, AON.CSS.aonBorderBottom());
		}
		
		AonTextBox name = new AonTextBox();
		AonTextBox surname  = new AonTextBox();
		surname.addStyleName(AON.CSS.aonMarginLeft());

		AonDocumentTextBox document = new AonDocumentTextBox();
		document.addValueChangeHandler( event -> {
				if (AonDocumentUtil.isEntity( document.getValue() )) {
					nameLabel.setText(AON.MSG.nameCompanyName());
					surnameLabel.setVisible(false);
					surname.setValue(null);
					surname.setVisible(false);
					name.setVisibleLength(45);
				} else {
					nameLabel.setText(AON.MSG.name());
					surnameLabel.setVisible(true);
					surname.setVisible(true);
					name.setVisibleLength(25);
				}
				callback.documentChanged(document.getValue());
		});
		tab.setWidget(0, 1, document);
		
		FlowPanel names = new FlowPanel();
		name.addValueChangeHandler( event -> callback.nameChanged(name.getValue()));
		name.setVisibleLength(25);
		name.setMaxLength(45);
		names.add(name);
		surname.addValueChangeHandler(event -> callback.surnameChanged(surname.getValue()));
		surname.setVisibleLength(25);
		surname.setMaxLength(40);
		names.add(surname);
		tab.setWidget(1, 1, names);
		
		AonTextBox phone = new AonTextBox();
		phone.addValueChangeHandler(event -> callback.phoneChanged(phone.getValue()));
		phone.setVisibleLength(9);
		phone.setMaxLength(9);
		tab.setWidget(2, 1, phone);
		
		FlowPanel address =  new FlowPanel();

		FlowPanel address1 =  new FlowPanel();
		address1.setStyleName(AON.CSS.aonWidthAll());
		InlineLabel stTypeLabel = new InlineLabel(AON.MSG.streetType());
		stTypeLabel.setStyleName(AON.CSS.aonInnerLabel());
		address1.add(stTypeLabel);
		AonTextBox streetInitial = new AonTextBox();
		streetInitial.addValueChangeHandler(event -> callback.streetInitialChanged(streetInitial.getValue()));
		streetInitial.setVisibleLength(3);
		streetInitial.setMaxLength(3);
		address1.add(streetInitial);

		InlineLabel stNameLabel = new InlineLabel(AON.MSG.streetName());
		stNameLabel.setStyleName(AON.CSS.aonInnerLabel());
		address1.add(stNameLabel);
		AonTextBox streetName = new AonTextBox();
		streetName.addValueChangeHandler(event -> callback.streetNameChanged(streetName.getValue()));
		streetName.setVisibleLength(40);
		streetName.setMaxLength(40);
		address1.add(streetName);
		address.add(address1);
		
		FlowPanel address2 =  new FlowPanel();
		address2.setStyleName(AON.CSS.aonWidthAll());
		InlineLabel stNumberLabel = new InlineLabel(AON.MSG.streetNumber());
		stNumberLabel.setStyleName(AON.CSS.aonInnerLabel());
		address2.add(stNumberLabel);
		AonTextBox streetNumber = new AonTextBox();
		streetNumber.addValueChangeHandler(event -> callback.streetNumberChanged(streetNumber.getValue()));
		streetNumber.setVisibleLength(5);
		streetNumber.setMaxLength(5);
		address2.add(streetNumber);

		InlineLabel stStairLabel = new InlineLabel(AON.MSG.streetStair());
		stStairLabel.setStyleName(AON.CSS.aonInnerLabel());
		address2.add(stStairLabel);
		AonTextBox streetStair = new AonTextBox();
		streetStair.addValueChangeHandler(event -> callback.streetStairChanged(streetStair.getValue()));
		streetStair.setVisibleLength(2);
		streetStair.setMaxLength(2);
		address2.add(streetStair);
		
		InlineLabel stFloorLabel = new InlineLabel(AON.MSG.streetFloor());
		stFloorLabel.setStyleName(AON.CSS.aonInnerLabel());
		address2.add(stFloorLabel);
		AonTextBox streetFloor = new AonTextBox();
		streetFloor.addValueChangeHandler(event -> callback.streetFloorChanged(streetFloor.getValue()));
		streetFloor.setVisibleLength(2);
		streetFloor.setMaxLength(2);
		address2.add(streetFloor);
		
		InlineLabel stDoorLabel = new InlineLabel(AON.MSG.streetDoor());
		stDoorLabel.setStyleName(AON.CSS.aonInnerLabel());
		address2.add(stDoorLabel);
		AonTextBox streetDoor = new AonTextBox();
		streetDoor.addValueChangeHandler(event -> callback.streetDoorChanged(streetDoor.getValue()));
		streetDoor.setVisibleLength(2);
		streetDoor.setMaxLength(2);
		address2.add(streetDoor);
		address.add(address2);
		tab.setWidget(3, 1, address);
		
		AonTextBox town = new AonTextBox();
		town.setVisibleLength(35);
		town.setMaxLength(35);
		town.addValueChangeHandler(event -> callback.townChanged(town.getValue()));
		tab.setWidget(4, 1, town);
		
		ProvinceListBox province = new ProvinceListBox();
		province.addChangeHandler(event -> callback.provinceChanged(Province.values()[province.getSelectedIndex()].getName()));
		tab.setWidget(5, 1, province);
		
		AonTextBox zip = new AonTextBox();
		zip.addValueChangeHandler(event -> callback.zipChanged(zip.getValue()));
		zip.setVisibleLength(5);
		zip.setMaxLength(5);
		tab.setWidget(6, 1, zip);
		
		AonTextBox contactPerson  = new AonTextBox();
		contactPerson.addValueChangeHandler( event -> callback.contactPersonChanged(contactPerson.getValue()));
		contactPerson.setVisibleLength(40);
		contactPerson.setMaxLength(40);
		tab.setWidget(7, 1, contactPerson);

		AonTextBox contactPhone  = new AonTextBox();
		contactPhone.addValueChangeHandler(event -> callback.contactPhoneChanged(contactPhone.getValue()));
		contactPhone.setVisibleLength(9);
		contactPhone.setMaxLength(9);
		tab.setWidget(8, 1, contactPhone);

		AonTextBox contactCellular = new AonTextBox();
		contactCellular.addValueChangeHandler(event -> callback.contactCellularChanged(contactCellular.getValue()));
		contactCellular.setVisibleLength(9);
		contactCellular.setMaxLength(9);
		tab.setWidget(9, 1, contactCellular);

		AonTextBox contactMail  = new AonTextBox();
		contactMail.addValueChangeHandler( event -> callback.contactMailChanged(contactMail.getValue()));
		contactMail.setVisibleLength(40);
		contactMail.setMaxLength(40);
		tab.setWidget(10, 1, contactMail);

		setWidget(tab);		
		
		document.setValue(callback.getDocument());

		name.setValue(callback.getName());
		surname.setValue(callback.getSurname());
		if (AonDocumentUtil.isEntity( document.getValue() )) {
			nameLabel.setText(AON.MSG.nameCompanyName());
			surnameLabel.setVisible(false);
			surname.setValue(null);
			surname.setVisible(false);
			name.setVisibleLength(45);
		} else {
			nameLabel.setText(AON.MSG.name());
			surnameLabel.setVisible(true);
			surname.setVisible(true);
			name.setVisibleLength(25);
		}
		
		phone.setValue(callback.getPhone());
		contactPerson.setValue(callback.getContactPerson());
		contactPhone.setValue(callback.getContactPhone());
		contactCellular.setValue(callback.getContactCellular());
		contactMail.setValue(callback.getContactEmail());
		streetInitial.setValue(callback.getStreetInitial());
		streetName.setValue(callback.getStreetName());
		streetNumber.setValue(callback.getStreetNumber());
		streetStair.setValue(callback.getStreetStair());
		streetFloor.setValue(callback.getStreetFloor());
		streetDoor.setValue(callback.getStreetDoor());
		town.setValue(callback.getTown());
		province.setSelectedIndex( Province.getByName(callback.getProvince()).ordinal());
		zip.setValue(callback.getZip());
	}
	
}
