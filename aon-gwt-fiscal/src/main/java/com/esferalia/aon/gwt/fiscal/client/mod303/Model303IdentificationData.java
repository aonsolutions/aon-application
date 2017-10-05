package com.esferalia.aon.gwt.fiscal.client.mod303;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.occam.api.model.type.Province;
import com.esferalia.aon.watson.util.AonDocumentUtil;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;

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
		setStyleName(AON.AON_CSS.aonScrollArea());
		FlexTable tab = new FlexTable();
		tab.setStyleName(AON.AON_CSS.aonPanelGrid());
		tab.addStyleName(AON.AON_CSS.aonWidthAll());
		tab.getColumnFormatter().setWidth(0, "300px");
		
		tab.getColumnFormatter().setWidth(1, "auto");
		tab.getColumnFormatter().setStyleName(1, AON.AON_CSS.aonPanelGridEven());
		
		tab.setWidget( 0, 0, new InlineLabel(AON.MSG.document()));
		InlineLabel nameLabel = new InlineLabel(AON.MSG.nameCompanyName());
		InlineLabel surnameLabel  = new InlineLabel(AON.MSG.surname());
		surnameLabel.setStyleName(AON.AON_CSS.aonMarginLeft());
		FlowPanel labels = new FlowPanel();
		labels.add(nameLabel);
		labels.add(surnameLabel);
		tab.setWidget( 1, 0, labels);
		tab.setWidget( 2, 0, new InlineLabel(AON.MSG.phone()));
		tab.setWidget( 3, 0, new InlineLabel(AON.MSG.address()));
		tab.setWidget( 4, 0, new InlineLabel(AON.MSG.town()));
		tab.setWidget( 5, 0, new InlineLabel(AON.MSG.province()));
		tab.setWidget( 6, 0, new InlineLabel(AON.MSG.zip()));
		tab.setWidget( 7, 0, new InlineLabel(AON.MSG.contactPerson()));
		tab.setWidget( 8, 0, new InlineLabel(AON.MSG.contactPhone()));
		tab.setWidget( 9, 0, new InlineLabel(AON.MSG.contactCellular()));
		tab.setWidget(10, 0, new InlineLabel(AON.MSG.contactMail()));
		
		for (int i = 0; i < 11; i++ ) {
			tab.getCellFormatter().setStyleName( i ,0, AON.AON_CSS.aonPanelGridOdd());	
			tab.getCellFormatter().setStyleName( i ,1, AON.AON_CSS.aonPanelGridEven());
		}

		TextBox name = new TextBox();
		name.setStyleName(AON.AON_CSS.aonInputText());
		TextBox surname  = new TextBox();
		surname.setStyleName(AON.AON_CSS.aonInputText());
		surname.addStyleName(AON.AON_CSS.aonMarginLeft());

		DocumentTextBox document = new DocumentTextBox();
		document.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
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
				callback.documentChanged(document.getValue());
			}
		});
		tab.setWidget(0, 1, document);
		
		FlowPanel names = new FlowPanel();
		name.addValueChangeHandler( new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.nameChanged(name.getValue());
			}
		});
		name.setVisibleLength(45);
		name.setMaxLength(45);
		names.add(name);
		surname.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.surnameChanged(surname.getValue());
			}
		});
		surname.setVisibleLength(40);
		surname.setMaxLength(40);
		names.add(surname);
		tab.setWidget(1, 1, names);
		
		TextBox phone = new TextBox();
		phone.setStyleName(AON.AON_CSS.aonInputText());
		phone.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.phoneChanged(phone.getValue());
			}
		});
		phone.setVisibleLength(9);
		phone.setMaxLength(9);
		tab.setWidget(2, 1, phone);
		
		FlowPanel address =  new FlowPanel();

		FlowPanel address1 =  new FlowPanel();
		address1.setStyleName(AON.AON_CSS.aonWidthAll());
		InlineLabel stTypeLabel = new InlineLabel(AON.MSG.streetType());
		stTypeLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		address1.add(stTypeLabel);
		TextBox streetInitial = new TextBox();
		streetInitial.setStyleName(AON.AON_CSS.aonInputText());
		streetInitial.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.streetInitialChanged(streetInitial.getValue());
			}
		});
		streetInitial.setVisibleLength(3);
		streetInitial.setMaxLength(3);
		address1.add(streetInitial);

		InlineLabel stNameLabel = new InlineLabel(AON.MSG.streetName());
		stNameLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		address1.add(stNameLabel);
		TextBox streetName = new TextBox();
		streetName.setStyleName(AON.AON_CSS.aonInputText());
		streetName.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.streetNameChanged(streetName.getValue());
			}
		});
		streetName.setVisibleLength(40);
		streetName.setMaxLength(40);
		address1.add(streetName);
		address.add(address1);
		
		FlowPanel address2 =  new FlowPanel();
		address2.setStyleName(AON.AON_CSS.aonWidthAll());
		InlineLabel stNumberLabel = new InlineLabel(AON.MSG.streetNumber());
		stNumberLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		address2.add(stNumberLabel);
		TextBox streetNumber = new TextBox();
		streetNumber.setStyleName(AON.AON_CSS.aonInputText());
		streetNumber.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.streetNumberChanged(streetNumber.getValue());
			}
		});

		streetNumber.setVisibleLength(5);
		streetNumber.setMaxLength(5);
		address2.add(streetNumber);

		InlineLabel stStairLabel = new InlineLabel(AON.MSG.streetStair());
		stStairLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		address2.add(stStairLabel);
		TextBox streetStair = new TextBox();
		streetStair.setStyleName(AON.AON_CSS.aonInputText());
		streetStair.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.streetStairChanged(streetStair.getValue());
			}
		});

		streetStair.setVisibleLength(2);
		streetStair.setMaxLength(2);
		address2.add(streetStair);
		
		InlineLabel stFloorLabel = new InlineLabel(AON.MSG.streetFloor());
		stFloorLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		address2.add(stFloorLabel);
		TextBox streetFloor = new TextBox();
		streetFloor.setStyleName(AON.AON_CSS.aonInputText());
		streetFloor.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.streetFloorChanged(streetFloor.getValue());
			}
		});
		streetFloor.setVisibleLength(2);
		streetFloor.setMaxLength(2);
		address2.add(streetFloor);
		
		InlineLabel stDoorLabel = new InlineLabel(AON.MSG.streetDoor());
		stDoorLabel.setStyleName(AON.AON_CSS.aonInnerLabel());
		address2.add(stDoorLabel);
		TextBox streetDoor = new TextBox();
		streetDoor.setStyleName(AON.AON_CSS.aonInputText());
		streetDoor.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.streetDoorChanged(streetDoor.getValue());
			}
		});

		streetDoor.setVisibleLength(2);
		streetDoor.setMaxLength(2);
		address2.add(streetDoor);
		address.add(address2);
		tab.setWidget(3, 1, address);
		
		TextBox town = new TextBox();
		town.setStyleName(AON.AON_CSS.aonInputText());
		town.setVisibleLength(35);
		town.setMaxLength(35);
		town.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.townChanged(town.getValue());
			}
		});
		//tab.setWidget(4, 1, phone);
		tab.setWidget(4, 1, town);
		
		ProvinceListBox province = new ProvinceListBox();
		province.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				callback.provinceChanged(Province.values()[province.getSelectedIndex()].getName());
			}
		});
		tab.setWidget(5, 1, province);
		
		TextBox zip = new TextBox();
		zip.setStyleName(AON.AON_CSS.aonInputText());
		zip.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.zipChanged(zip.getValue());
			}
		});
		zip.setVisibleLength(5);
		zip.setMaxLength(5);
		tab.setWidget(6, 1, zip);
		
		TextBox contactPerson  = new TextBox();
		contactPerson.setStyleName(AON.AON_CSS.aonInputText());
		contactPerson.addValueChangeHandler( new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.contactPersonChanged(contactPerson.getValue());
			}
		});
		contactPerson.setVisibleLength(40);
		contactPerson.setMaxLength(40);
		//tab.setWidget(7, 1, zip);
		tab.setWidget(7, 1, contactPerson);

		TextBox contactPhone  = new TextBox();
		contactPhone.setStyleName(AON.AON_CSS.aonInputText());
		contactPhone.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.contactPhoneChanged(contactPhone.getValue());
			}
		});
		contactPhone.setVisibleLength(9);
		contactPhone.setMaxLength(9);
		tab.setWidget(8, 1, contactPhone);

		TextBox contactCellular = new TextBox();
		contactCellular.setStyleName(AON.AON_CSS.aonInputText());
		contactCellular.addValueChangeHandler(new ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.contactCellularChanged(contactCellular.getValue());
			}
		});
		contactCellular.setVisibleLength(9);
		contactCellular.setMaxLength(9);
		tab.setWidget(9, 1, contactCellular);

		TextBox contactMail  = new TextBox();
		contactMail.setStyleName(AON.AON_CSS.aonInputText());
		contactMail.addValueChangeHandler( new  ValueChangeHandler<String>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				callback.contactMailChanged(contactMail.getValue());
			}
		});
		contactMail.setVisibleLength(40);
		contactMail.setMaxLength(40);
		tab.setWidget(10, 1, contactMail);

		setWidget(tab);		
		
		document.setValue(callback.getDocument());
//		document.setEnabled(!callback.isFinished());

		name.setValue(callback.getName());
//		name.setEnabled(!callback.isFinished());
		surname.setValue(callback.getSurname());
//		surname.setEnabled(!callback.isFinished());
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
		
		phone.setValue(callback.getPhone());
//		phone.setEnabled(!callback.isFinished());
		contactPerson.setValue(callback.getContactPerson());
//		contactPerson.setEnabled(!callback.isFinished());
		contactPhone.setValue(callback.getContactPhone());
//		contactPhone.setEnabled(!callback.isFinished());
		contactCellular.setValue(callback.getContactCellular());
//		contactCellular.setEnabled(!callback.isFinished());
		contactMail.setValue(callback.getContactEmail());
//		contactMail.setEnabled(!callback.isFinished());
		streetInitial.setValue(callback.getStreetInitial());
//		streetInitial.setEnabled(!callback.isFinished());
		streetName.setValue(callback.getStreetName());
//		streetName.setEnabled(!callback.isFinished());
		streetNumber.setValue(callback.getStreetNumber());
//		streetNumber.setEnabled(!callback.isFinished());
		streetStair.setValue(callback.getStreetStair());
//		streetStair.setEnabled(!callback.isFinished());
		streetFloor.setValue(callback.getStreetFloor());
//		streetFloor.setEnabled(!callback.isFinished());
		streetDoor.setValue(callback.getStreetDoor());
//		streetDoor.setEnabled(!callback.isFinished());
		town.setValue(callback.getTown());
//		town.setEnabled(!callback.isFinished());
		province.setSelectedIndex( Province.getByName(callback.getProvince()).ordinal());
//		province.setEnabled(!callback.isFinished());
		zip.setValue(callback.getZip());
//		zip.setEnabled(!callback.isFinished());
	}
	
}
