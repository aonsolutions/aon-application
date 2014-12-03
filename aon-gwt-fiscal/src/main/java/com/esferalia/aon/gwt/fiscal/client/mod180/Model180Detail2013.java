package com.esferalia.aon.gwt.fiscal.client.mod180;

import static com.esferalia.aon.gwt.fiscal.client.mod180.Model180.MSG;

import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleTextBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerTextBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.occam.api.model.Mod180Detail;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Model180Detail2013 extends ResizeComposite {

	interface Model180Detail2013Binder extends UiBinder<Widget, Model180Detail2013> {}
	private static Model180Detail2013Binder MODEL180_DETAIL_2013_BINDER 
		= GWT.create(Model180Detail2013Binder.class);

	static interface ICallBack {
		void redrawList( Mod180Detail detail);
	}
	
	Mod180Detail detail;
	private ICallBack callback;
	
	@UiField
	Button deleteDetailButton;
	@UiField
	Button restoreDeletedButton;

	@UiField
	DocumentTextBox receiverDocument;
	@UiField
	DocumentTextBox representativeDocument;
	@UiField
	TextBox fullName;
	@UiField
	IntegerTextBox accrualYear;
	@UiField
	ProvinceListBox province;
	@UiField
	DoubleTextBox perception;
	@UiField
	DoubleTextBox retention;
	@UiField
	DoubleTextBox percent;
	@UiField
	CheckBox inKind;
	
	@UiField
	ListBox location;
	@UiField
	TextBox cadasdralReference;
	@UiField
	TextBox streetType;
	@UiField
	TextBox streetName;
	@UiField
	TextBox numberType;
	@UiField
	TextBox number;
	@UiField
	TextBox numberSuffix;
	@UiField
	TextBox block;
	@UiField
	TextBox hall;
	@UiField
	TextBox stair;
	@UiField
	TextBox floor;
	@UiField
	TextBox door;
	@UiField
	TextBox complement;
	@UiField
	TextBox city;
	@UiField
	TextBox town;
	@UiField
	TextBox townCode;
	@UiField
	ProvinceListBox provinceCode;
	@UiField
	TextBox zip;
	
	
	public Model180Detail2013() {
		Widget ui = MODEL180_DETAIL_2013_BINDER.createAndBindUi(this);
		initWidget(ui);
		location.addItem( MSG.buildingLocationValue(0) );
		location.addItem( MSG.buildingLocationValue(1) );	
		location.addItem( MSG.buildingLocationValue(2) );
		location.addItem( MSG.buildingLocationValue(3) );
	}

	public void setCallback(ICallBack callback) {
		this.callback = callback;
	}
	
	public void setDetail(Mod180Detail detail) {
		this.detail = detail;

		receiverDocument.setValue(detail.getDocument());
		representativeDocument.setValue(detail.getRepresentativeDocument());
		fullName.setValue(detail.getName());
		accrualYear.setValue(detail.getAccrualYear());
		province.setSelectedIndex(detail.getProvince());
		perception.setValue(detail.getPerception());
		retention.setValue(detail.getRetention());
		percent.setValue(detail.getPercent());
		inKind.setValue(detail.isInKind());
		if (detail.getLocation() != null)
			try {
				location.setSelectedIndex( Integer.parseInt(detail.getLocation()) );
			} catch (NumberFormatException e) {
				location.setSelectedIndex( 0 );
			}
		cadasdralReference.setValue(detail.getCadasdralReference() );
		streetType.setValue(detail.getStreetType() );
		streetName.setValue(detail.getStreetName() );
		numberType.setValue(detail.getNumberType() );
		number.setValue(detail.getNumber() );
		numberSuffix.setValue(detail.getNumberSuffix() );
		block.setValue(detail.getBlock() );
		hall.setValue(detail.getHall() );
		stair.setValue(detail.getStair() );
		floor.setValue(detail.getFloor() );
		door.setValue(detail.getDoor() );
		complement.setValue(detail.getComplement() );
		city.setValue(detail.getCity() );
		town.setValue(detail.getTown() );
		townCode.setValue(detail.getTownCode() );
		if (detail.getProvinceCode() != null)
			try {
				provinceCode.setSelectedIndex( Integer.parseInt(detail.getProvinceCode()) );
			} catch (NumberFormatException e) {
				provinceCode.setSelectedIndex( 0 );
			}
		zip.setValue(detail.getZip() );
		
		restoreDeletedButton.setVisible(detail.isDeleted());
		deleteDetailButton.setVisible(!detail.isDeleted());
	}

	@UiHandler("receiverDocument")
	void onChangeReceiverDocument(ChangeEvent event) {
		detail.setDocument(receiverDocument.getValue());
		detail.setDirty(true);
	}

	@UiHandler("representativeDocument")
	void onChangeRepresentativeDocument(ChangeEvent event) {
		detail.setRepresentativeDocument(representativeDocument.getValue());
		detail.setDirty(true);
	}

	@UiHandler("fullName")
	void onChangeFullName(ChangeEvent event) {
		detail.setName(fullName.getValue());
		detail.setDirty(true);
	}

	@UiHandler("accrualYear")
	void onChangeAccrualYear(ChangeEvent event) {
		detail.setAccrualYear(accrualYear.getIntValue());
		detail.setDirty(true);
	}

	@UiHandler("province")
	void onChangeProvince(ChangeEvent event) {
		detail.setProvince(province.getSelectedIndex());
		detail.setDirty(true);
	}

	@UiHandler("perception")
	void onChangePerception(ChangeEvent event) {
		detail.setPerception(perception.getDoubleValue());
		detail.setDirty(true);
	}

	@UiHandler("retention")
	void onChangeRetention(ChangeEvent event) {
		detail.setRetention(retention.getDoubleValue());
		detail.setDirty(true);
	}

	@UiHandler("percent")
	void onChangePercent(ChangeEvent event) {
		double ret = retention.getDoubleValue();
		boolean retChanged = false;
		if (ret == 0) {
			ret = AonMathUtils.round(perception.getDoubleValue() * percent.getDoubleValue() / 100);
			retention.setValue(ret);
			retChanged = true;
		}
		detail.setPercent(percent.getDoubleValue());
		if (retChanged) {
			detail.setRetention(retention.getDoubleValue());
		}
		detail.setDirty(true);
	}

	@UiHandler("inKind")
	void onChangeInKind(ClickEvent event) {
		detail.setInKind(inKind.getValue());
		detail.setDirty(true);
	}
	
	@UiHandler("location")
	void onChangeLocation(ChangeEvent event) {
		detail.setLocation(Integer.toString( location.getSelectedIndex()));
		detail.setDirty(true);
	}
	@UiHandler("cadasdralReference")
	void onChangeCadasdralReference(ChangeEvent event) {
		detail.setCadasdralReference(cadasdralReference.getValue());
		detail.setDirty(true);
	}
	@UiHandler("streetType")
	void onChangeStreetType(ChangeEvent event) {
		detail.setStreetType(streetType.getValue());
		detail.setDirty(true);
	}
	@UiHandler("streetName")
	void onChangeStreetName(ChangeEvent event) {
		detail.setStreetName(streetName.getValue());
		detail.setDirty(true);
	}
	@UiHandler("numberType")
	void onChangeNumberType(ChangeEvent event) {
		detail.setNumberType(numberType.getValue());
		detail.setDirty(true);
	}
	@UiHandler("number")
	void onChangeNumber(ChangeEvent event) {
		detail.setNumber(number.getValue());
		detail.setDirty(true);
	}
	@UiHandler("numberSuffix")
	void onChangeNumberSuffix(ChangeEvent event) {
		detail.setNumberSuffix(numberSuffix.getValue());
		detail.setDirty(true);
	}
	@UiHandler("block")
	void onChangeBlock(ChangeEvent event) {
		detail.setBlock(block.getValue());
		detail.setDirty(true);
	}
	@UiHandler("hall")
	void onChangeHall(ChangeEvent event) {
		detail.setHall(hall.getValue());
		detail.setDirty(true);
	}
	@UiHandler("stair")
	void onChangeStair(ChangeEvent event) {
		detail.setStair(stair.getValue());
		detail.setDirty(true);
	}
	@UiHandler("floor")
	void onChangeFloor(ChangeEvent event) {
		detail.setFloor(floor.getValue());
		detail.setDirty(true);
	}
	@UiHandler("door")
	void onChangeDoor(ChangeEvent event) {
		detail.setDoor(door.getValue());
		detail.setDirty(true);
	}
	@UiHandler("complement")
	void onChangeComplement(ChangeEvent event) {
		detail.setComplement(complement.getValue());
		detail.setDirty(true);
	}
	@UiHandler("city")
	void onChangeCity(ChangeEvent event) {
		detail.setCity(city.getValue());
		detail.setDirty(true);
	}
	@UiHandler("town")
	void onChangeTown(ChangeEvent event) {
		detail.setTown(town.getValue());
		detail.setDirty(true);
	}
	@UiHandler("townCode")
	void onChangeTownCode(ChangeEvent event) {
		detail.setTownCode(townCode.getValue());
		detail.setDirty(true);
	}
	@UiHandler("provinceCode")
	void onChangeProvinceCode(ChangeEvent event) {
		detail.setProvinceCode(Integer.toString(provinceCode.getSelectedIndex()));
		detail.setDirty(true);
	}
	@UiHandler("zip")
	void onChangeZip(ChangeEvent event) {
		detail.setZip(zip.getValue());
		detail.setDirty(true);
	}

	@UiHandler("deleteDetailButton")
	void onDeleteDetailButtonClick(ClickEvent event) {
		detail.setDeleted(true);
		restoreDeletedButton.setVisible(true);
		deleteDetailButton.setVisible(false);
		callback.redrawList(detail);
	}

	@UiHandler("restoreDeletedButton")
	void onRestoreDeletedButtonClick(ClickEvent event) {
		detail.setDeleted(false);
		if (!detail.isDirty()) {
			restoreDeletedButton.setVisible(false);
			deleteDetailButton.setVisible(true);
			callback.redrawList(detail);
		}
	};

}
