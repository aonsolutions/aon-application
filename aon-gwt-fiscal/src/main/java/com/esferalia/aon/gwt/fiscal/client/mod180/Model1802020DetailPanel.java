package com.esferalia.aon.gwt.fiscal.client.mod180;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.StreetTypeListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonIntegerBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180Detail2020.IModel180DetailCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model1802020DetailPanel extends SimpleLayoutPanel implements Focusable {
	
	private static final String WIDTH_150PX = "150px";
	private static final String WIDTH_100PX = "100px";
	private int tabIndex; 
	private AonDocumentTextBox document;
	
	public Model1802020DetailPanel(Mod180Detail detail, IModel180DetailCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonWidthAll());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonScrollArea());
		
		FlexTable tab1 = new FlexTable();
		tab1.getColumnFormatter().setWidth(0, WIDTH_100PX);
		tab1.getColumnFormatter().setWidth(1, WIDTH_100PX);
		tab1.getColumnFormatter().setWidth(2, "300px");
		tab1.getColumnFormatter().setWidth(3, "auto");
		
		tab1.setStyleName(AON.CSS.aonWidthAll());
		tab1.addStyleName(AON.CSS.aonNowrap());
		tab1.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab1.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab1.getFlexCellFormatter().setColSpan(0, 0, 4);
		tab1.setWidget(0, 0, new InlineLabel(AON.MSG.perceptionData()));

 		tab1.setWidget(1, 0, new Model180SmallerLabel(AON.MSG.receiverDocument()));
		tab1.setWidget(1, 1, new Model180SmallerLabel(AON.MSG.representativeDocument()));
		tab1.setWidget(1, 2, new Model180SmallerLabel(AON.MSG.fullName()));
		tab1.setWidget(1, 3, new Model180SmallerLabel(AON.MSG.province()));
		
		document = new AonDocumentTextBox();
		document.setValue(detail.getDocument());
		document.addValueChangeHandler(event -> {
			detail.setDocument(document.getValue());
			callback.onValueChanged(detail);
		});
		tab1.setWidget(2, 0, document);
		
		AonDocumentTextBox representativeDocument = new AonDocumentTextBox();
		representativeDocument.setValue(detail.getRepresentativeDocument());
		representativeDocument.addValueChangeHandler(event -> {
			detail.setRepresentativeDocument(representativeDocument.getValue());
			callback.onValueChanged(detail);
		});
		tab1.setWidget(2, 1, representativeDocument);
		
		AonTextBox name = new AonTextBox();
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.setValue(detail.getName());
		name.addValueChangeHandler(event -> {
			detail.setName(name.getValue());
			callback.onNameChanged(detail);
		});
		tab1.setWidget(2, 2, name);

		ProvinceListBox province = new ProvinceListBox();
		province.setSelectedIndex(detail.getProvince());
		province.addChangeHandler( event -> {
			detail.setProvince(province.getSelectedIndex());
			callback.onValueChanged(detail);
		});
		tab1.setWidget(2, 3, province);
		panel.add(tab1);
		
		FlexTable tab2 = new FlexTable();
		tab2.getColumnFormatter().setWidth(0, WIDTH_100PX);
		tab2.getColumnFormatter().setWidth(1, WIDTH_100PX);
		tab2.getColumnFormatter().setWidth(2, WIDTH_100PX);
		tab2.getColumnFormatter().setWidth(3, WIDTH_100PX);
		tab2.getColumnFormatter().setWidth(4, "auto");
		
		tab2.setStyleName(AON.CSS.aonWidthAll());
		tab2.addStyleName(AON.CSS.aonNowrap());

 		tab2.setWidget(0, 0, new Label());
		tab2.setWidget(0, 1, new Model180SmallerLabel(AON.MSG.perception()));
		tab2.setWidget(0, 2, new Model180SmallerLabel(AON.MSG.percent()));
		tab2.setWidget(0, 3, new Model180SmallerLabel(AON.MSG.retention()));
		tab2.setWidget(0, 4, new Model180SmallerLabel(AON.MSG.accrualYear()));
		
		CheckBox inKind = new CheckBox(AON.MSG.inKind());
		inKind.setValue(detail.isInKind());
		inKind.addClickHandler(event -> {
			detail.setInKind(inKind.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(1, 0, inKind);
		
		AonDoubleBox perception = new AonDoubleBox();
		perception.setValue(detail.getPerception());
		perception.addValueChangeHandler(event -> {
			detail.setPerception(perception.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(1, 1, perception);
		
		AonDoubleBox percent = new AonDoubleBox();
		AonDoubleBox retention = new AonDoubleBox();
		percent.setValue(detail.getPercent());
		percent.addValueChangeHandler(event -> {
			double ret = retention.getValue();
			if (ret == 0) {
				ret = AonMathUtils.round(perception.getValue() * percent.getValue() / 100);
				retention.setValue(ret,false);
				detail.setRetention(retention.getValue());
			}
			detail.setPercent(percent.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(1, 2, percent);
		
		retention.setValue(detail.getRetention());
		retention.addValueChangeHandler(event -> {
			detail.setRetention(retention.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(1, 3, retention);

		
		AonIntegerBox accrualYear = new AonIntegerBox();
		accrualYear.setMaxLength(4);
		accrualYear.setVisibleLength(4);
		accrualYear.setValue(detail.getAccrualYear());
		accrualYear.addValueChangeHandler(event -> {
			detail.setAccrualYear(accrualYear.getValue());
			callback.onValueChanged(detail);
		});
		tab2.setWidget(1, 4, accrualYear);
		panel.add(tab2);
		
		
		FlexTable tab3 = new FlexTable();
		tab3.getColumnFormatter().setWidth(0, "300px");
		tab3.getColumnFormatter().setWidth(1, "auto");
		
		tab3.setStyleName(AON.CSS.aonWidthAll());
		tab3.addStyleName(AON.CSS.aonMarginTop());
		tab3.addStyleName(AON.CSS.aonNowrap());
		
		tab3.getCellFormatter().setStyleName(0, 0, AON.CSS.aonBorderBottom());
		tab3.getCellFormatter().addStyleName(0, 0, AON.CSS.aonBold());
		tab3.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab3.setWidget(0, 0, new InlineLabel(AON.MSG.buildingData()));

		tab3.setWidget(1, 0, new Model180SmallerLabel(AON.MSG.buildingLocation()));
		tab3.setWidget(1, 1, new Model180SmallerLabel(AON.MSG.cadasdralReference()));
		
		ListBox location = new ListBox();
		location.addItem( AON.MSG.buildingLocationValue2020(0) );
		location.addItem( AON.MSG.buildingLocationValue2020(1) );	
		location.addItem( AON.MSG.buildingLocationValue2020(2) );
		location.addItem( AON.MSG.buildingLocationValue2020(3) );
		location.addItem( AON.MSG.buildingLocationValue2020(4) );
		location.setSelectedIndex( 0 );
		if (AonStringUtils.isNotBlank(detail.getLocation())) {
			try {
				location.setSelectedIndex( Integer.parseInt(detail.getLocation()) );
			} catch (NumberFormatException e) { 
				// nothing 
			}
		}
		location.addChangeHandler(event -> {
			detail.setLocation(Integer.toString( location.getSelectedIndex()));
			callback.onValueChanged(detail);
		});
		tab3.setWidget(2, 0, location);
		
		AonTextBox cadasdralReference = new AonTextBox();
		cadasdralReference.setVisibleLength(23);
		cadasdralReference.setMaxLength(20);
		cadasdralReference.setValue(detail.getCadasdralReference());
		cadasdralReference.addValueChangeHandler(event -> {
			detail.setCadasdralReference(cadasdralReference.getValue());
			callback.onValueChanged(detail);				
		});
		tab3.setWidget(2, 1, cadasdralReference);
		panel.add(tab3);
		
		FlexTable tab4 = new FlexTable();
		tab4.setStyleName(AON.CSS.aonWidthAll());
		tab4.addStyleName(AON.CSS.aonNowrap());
		
		tab4.getColumnFormatter().setWidth(0, "200px");
		tab4.getColumnFormatter().setWidth(1, "auto");
		
		tab4.setWidget(0, 0, new Model180SmallerLabel(AON.MSG.streetType()));
		tab4.setWidget(0, 1, new Model180SmallerLabel(AON.MSG.streetName()));

		StreetTypeListBox streetType = new StreetTypeListBox();
		streetType.setValue(StreetType.getForIneCode(detail.getStreetType()));
		streetType.addChangeHandler( event -> {
			detail.setStreetType(streetType.getValue(streetType.getSelectedIndex()));
			callback.onValueChanged(detail);
		});
		tab4.setWidget(1, 0, streetType);
		
		AonTextBox streetName = new AonTextBox();
		streetName.setVisibleLength(23);
		streetName.setMaxLength(20);
		streetName.setValue(detail.getStreetName());
		streetName.addValueChangeHandler(event -> {
			detail.setStreetName(streetName.getValue());
			callback.onValueChanged(detail);				
		});
		tab4.setWidget(1, 1, streetName);
		panel.add(tab4);
		
		FlexTable tab5 = new FlexTable();
		tab5.setStyleName(AON.CSS.aonWidthAll());
		tab5.addStyleName(AON.CSS.aonNowrap());
		
		tab5.getColumnFormatter().setWidth(0, "70px");
		tab5.getColumnFormatter().setWidth(1, "60px");
		tab5.getColumnFormatter().setWidth(2, "60px");
		tab5.getColumnFormatter().setWidth(3, "60px");
		tab5.getColumnFormatter().setWidth(4, "60px");
		tab5.getColumnFormatter().setWidth(5, "60px");
		tab5.getColumnFormatter().setWidth(6, "60px");
		tab5.getColumnFormatter().setWidth(7, "60px");
		tab5.getColumnFormatter().setWidth(8, "auto");
		
		tab5.setWidget(0, 0, new Model180SmallerLabel(AON.MSG.streetNumberType()));
		tab5.setWidget(0, 1, new Model180SmallerLabel(AON.MSG.streetNumber()));
		tab5.setWidget(0, 2, new Model180SmallerLabel(AON.MSG.streetNumberSuffix()));
		tab5.setWidget(0, 3, new Model180SmallerLabel(AON.MSG.streetBlock()));
		tab5.setWidget(0, 4, new Model180SmallerLabel(AON.MSG.streetHall()));
		tab5.setWidget(0, 5, new Model180SmallerLabel(AON.MSG.streetStair()));
		tab5.setWidget(0, 6, new Model180SmallerLabel(AON.MSG.streetFloor()));
		tab5.setWidget(0, 7, new Model180SmallerLabel(AON.MSG.streetDoor()));
		tab5.setWidget(0, 8, new Model180SmallerLabel(AON.MSG.streetComplement()));

		AonTextBox numberType = new AonTextBox();
		numberType.setMaxLength(3);
		numberType.setVisibleLength(3);
		numberType.setValue(detail.getNumberType());
		numberType.addValueChangeHandler(event -> {
			detail.setNumberType(numberType.getValue());
			callback.onValueChanged(detail);				
		});
		tab5.setWidget(1, 0, numberType);
		
		AonTextBox number = new AonTextBox();
		number.setMaxLength(5);
		number.setVisibleLength(5);
		number.setValue(detail.getNumber());
		number.addValueChangeHandler(event -> {
			detail.setNumber(number.getValue());
			callback.onValueChanged(detail);				
		});
		tab5.setWidget(1, 1, number);
		
		AonTextBox numberSuffix = new AonTextBox();
		numberSuffix.setMaxLength(3);
		numberSuffix.setVisibleLength(3);
		numberSuffix.setValue(detail.getNumberSuffix());
		numberSuffix.addValueChangeHandler(event -> {
			detail.setNumberSuffix(numberSuffix.getValue());
			callback.onValueChanged(detail);				
		});
		tab5.setWidget(1, 2, numberSuffix);
		
		AonTextBox block = new AonTextBox();
		block.setMaxLength(3);
		block.setVisibleLength(3);
		block.setValue(detail.getBlock());
		block.addValueChangeHandler(event -> {
			detail.setBlock(block.getValue());
			callback.onValueChanged(detail);				
		});
		tab5.setWidget(1, 3, block);
		
		AonTextBox hall = new AonTextBox();
		hall.setMaxLength(3);
		hall.setVisibleLength(3);
		hall.setValue(detail.getHall());
		hall.addValueChangeHandler(event -> {
			detail.setHall(hall.getValue());
			callback.onValueChanged(detail);				
		});
		tab5.setWidget(1, 4, hall);
		
		AonTextBox stair = new AonTextBox();
		stair.setMaxLength(3);
		stair.setVisibleLength(3);
		stair.setValue(detail.getStair());
		stair.addValueChangeHandler(event -> {
			detail.setStair(stair.getValue());
			callback.onValueChanged(detail);				
		});
		tab5.setWidget(1, 5, stair);
		
		AonTextBox floor = new AonTextBox();
		floor.setMaxLength(3);
		floor.setVisibleLength(3);
		floor.setValue(detail.getFloor());
		floor.addValueChangeHandler(event -> {
			detail.setFloor(floor.getValue());
			callback.onValueChanged(detail);				
		});
		tab5.setWidget(1, 6, floor);
		
		AonTextBox door = new AonTextBox();
		door.setMaxLength(3);
		door.setVisibleLength(3);
		door.setValue(detail.getDoor());
		door.addValueChangeHandler(event -> {
			detail.setDoor(door.getValue());
			callback.onValueChanged(detail);				
		});
		tab5.setWidget(1, 7, door);
		
		AonTextBox complement = new AonTextBox();
		complement.setMaxLength(40);
		complement.setVisibleLength(20);
		complement.setValue(detail.getComplement());
		complement.addValueChangeHandler(event -> {
			detail.setComplement(complement.getValue());
			callback.onValueChanged(detail);				
		});
		tab5.setWidget(1, 8, complement);
		
		panel.add(tab5);
		
		FlexTable tab6 = new FlexTable();
		tab6.setStyleName(AON.CSS.aonWidthAll());
		tab6.addStyleName(AON.CSS.aonNowrap());
		
		tab6.getColumnFormatter().setWidth(0, WIDTH_150PX);
		tab6.getColumnFormatter().setWidth(1, WIDTH_150PX);
		tab6.getColumnFormatter().setWidth(2, WIDTH_100PX);
		tab6.getColumnFormatter().setWidth(3, WIDTH_150PX);
		tab6.getColumnFormatter().setWidth(4, "auto");
		
		tab6.setWidget(0, 0, new Model180SmallerLabel(AON.MSG.city()));
		tab6.setWidget(0, 1, new Model180SmallerLabel(AON.MSG.town()));
		tab6.setWidget(0, 2, new Model180SmallerLabel(AON.MSG.townCode()));
		tab6.setWidget(0, 3, new Model180SmallerLabel(AON.MSG.province()));
		tab6.setWidget(0, 4, new Model180SmallerLabel(AON.MSG.zip()));
		
		AonTextBox city = new AonTextBox();
		city.setMaxLength(20);
		city.setVisibleLength(20);
		city.setValue(detail.getCity());
		city.addValueChangeHandler(event -> {
			detail.setCity(city.getValue());
			callback.onValueChanged(detail);				
		});
		tab6.setWidget(1, 0, city);

		AonTextBox town = new AonTextBox();
		town.setMaxLength(30);
		town.setVisibleLength(20);
		town.setValue(detail.getTown());
		town.addValueChangeHandler(event -> {
			detail.setTown(town.getValue());
			callback.onValueChanged(detail);				
		});
		tab6.setWidget(1, 1, town);

		FlowPanel townCodePanel = new FlowPanel();
		townCodePanel.setStyleName(AON.CSS.aonFlexBlock());
		AonTextBox townCode = new AonTextBox();
		townCode.setMaxLength(5);
		townCode.setVisibleLength(5);
		townCode.setValue(detail.getTownCode());
		townCode.addValueChangeHandler(event -> {
			detail.setTownCode(townCode.getValue());
			callback.onValueChanged(detail);				
		});
		townCodePanel.add(townCode);
		
		Anchor townCodeAnchor = new Anchor();
		townCodeAnchor.setStyleName(AON.CSS.aonIconLabel());
		townCodeAnchor.addStyleName(AON.CSS.aonIconLink());
		townCodeAnchor.setTarget("_blank");
		townCodeAnchor.setHref("https://www.ine.es/dyngs/INEbase/es/operacion.htm?c=Estadistica_C&cid=1254736177031&menu=ultiDatos&idp=1254734710990");		
		townCodePanel.add(townCodeAnchor);
		
		tab6.setWidget(1, 2, townCodePanel);
		

		ProvinceListBox provinceCode = new ProvinceListBox();
		if (AonStringUtils.isNotBlank(detail.getProvinceCode())) {
			try {
				provinceCode.setSelectedIndex( Integer.parseInt(detail.getProvinceCode()) );
			} catch (NumberFormatException e) {
				provinceCode.setSelectedIndex( 0 );
			}
		} else {
			provinceCode.setSelectedIndex( 0 );
		}
		provinceCode.addChangeHandler( event -> {
			detail.setProvinceCode(Integer.toString(provinceCode.getSelectedIndex()));
			callback.onValueChanged(detail);
		});
		tab6.setWidget(1, 3, provinceCode);

		AonTextBox zip = new AonTextBox();
		zip.setMaxLength(5);
		zip.setVisibleLength(5);
		zip.setValue(detail.getZip());
		zip.addValueChangeHandler(event -> {
			detail.setZip(zip.getValue());
			callback.onValueChanged(detail);				
		});
		tab6.setWidget(1, 4, zip);
		panel.add(tab6);

		scroll.setWidget(panel);
		setWidget(scroll);
	}

	@Override
	public int getTabIndex() {
		return tabIndex;
	}

	@Override
	public void setAccessKey(char key) {
		// Nothing
	}

	@Override
	public void setFocus(boolean focused) {
		document.selectAll();
		document.setFocus(true);
	}

	@Override
	public void setTabIndex(int index) {
		tabIndex = index;
	}
	
}
