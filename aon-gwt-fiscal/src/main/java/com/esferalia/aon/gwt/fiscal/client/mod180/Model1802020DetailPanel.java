package com.esferalia.aon.gwt.fiscal.client.mod180;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.IntegerBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.StreetTypeListBox;
import com.esferalia.aon.gwt.fiscal.client.mod180.Model180Detail2020.IModel180DetailCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod180Detail;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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
import com.google.gwt.user.client.ui.TextBox;

public class Model1802020DetailPanel extends SimpleLayoutPanel implements Focusable {
	
	private static class MediumLabel extends InlineLabel {
		private MediumLabel(String label) {
			super(label);
			setStyleName(AON.AON_CSS.aonFontMedium());
		}
	}
	private int tabIndex; 
	private DocumentTextBox document;
	
	public Model1802020DetailPanel(Mod180Detail detail, IModel180DetailCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		FlexTable tab1 = new FlexTable();
		tab1.getColumnFormatter().setWidth(0, "100px");
		tab1.getColumnFormatter().setWidth(1, "100px");
		tab1.getColumnFormatter().setWidth(2, "300px");
		tab1.getColumnFormatter().setWidth(3, "auto");
		
		tab1.setStyleName(AON.AON_CSS.aonWidthAll());
		tab1.addStyleName(AON.AON_CSS.aonNowrap());
		tab1.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonBorderBottom());
		tab1.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		tab1.getFlexCellFormatter().setColSpan(0, 0, 4);
		tab1.setWidget(0, 0, new InlineLabel(AON.MSG.perceptionData()));

 		tab1.setWidget(1, 0, new MediumLabel(AON.MSG.receiverDocument()));
		tab1.setWidget(1, 1, new MediumLabel(AON.MSG.representativeDocument()));
		tab1.setWidget(1, 2, new MediumLabel(AON.MSG.fullName()));
		tab1.setWidget(1, 3, new MediumLabel(AON.MSG.province()));
		
		document = new DocumentTextBox();
		document.setValue(detail.getDocument());
		document.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setDocument(document.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab1.setWidget(2, 0, document);
		
		DocumentTextBox representativeDocument = new DocumentTextBox();
		representativeDocument.setValue(detail.getRepresentativeDocument());
		representativeDocument.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setRepresentativeDocument(representativeDocument.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab1.setWidget(2, 1, representativeDocument);
		
		TextBox name = new TextBox();
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.setStyleName(AON.AON_CSS.aonInputText());
		name.setValue(detail.getName());
		name.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setName(name.getValue());
				callback.onNameChanged(detail);
			}
		});
		tab1.setWidget(2, 2, name);

		ProvinceListBox province = new ProvinceListBox();
		province.setSelectedIndex(detail.getProvince());
		province.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				detail.setProvince(province.getSelectedIndex());
				callback.onValueChanged(detail);
			}
		});
		tab1.setWidget(2, 3, province);
		panel.add(tab1);
		
		FlexTable tab2 = new FlexTable();
		tab2.getColumnFormatter().setWidth(0, "100px");
		tab2.getColumnFormatter().setWidth(1, "100px");
		tab2.getColumnFormatter().setWidth(2, "100px");
		tab2.getColumnFormatter().setWidth(3, "100px");
		tab2.getColumnFormatter().setWidth(4, "auto");
		
		tab2.setStyleName(AON.AON_CSS.aonWidthAll());
		tab2.addStyleName(AON.AON_CSS.aonNowrap());

 		tab2.setWidget(0, 0, new Label());
		tab2.setWidget(0, 1, new MediumLabel(AON.MSG.perception()));
		tab2.setWidget(0, 2, new MediumLabel(AON.MSG.percent()));
		tab2.setWidget(0, 3, new MediumLabel(AON.MSG.retention()));
		tab2.setWidget(0, 4, new MediumLabel(AON.MSG.accrualYear()));
		
		CheckBox inKind = new CheckBox(AON.MSG.inKind());
		inKind.setValue(detail.isInKind());
		inKind.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				detail.setInKind(inKind.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(1, 0, inKind);
		
		DoubleBox perception = new DoubleBox();
		perception.setValue(detail.getPerception());
		perception.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setPerception(perception.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(1, 1, perception);
		
		DoubleBox percent = new DoubleBox();
		DoubleBox retention = new DoubleBox();
		percent.setValue(detail.getPercent());
		percent.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				double ret = retention.getValue();
				if (ret == 0) {
					ret = AonMathUtils.round(perception.getValue() * percent.getValue() / 100);
					retention.setValue(ret,false);
					detail.setRetention(retention.getValue());
				}
				detail.setPercent(percent.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(1, 2, percent);
		
		retention.setValue(detail.getRetention());
		retention.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				detail.setRetention(retention.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(1, 3, retention);

		
		IntegerBox accrualYear = new IntegerBox();
		accrualYear.setMaxLength(4);
		accrualYear.setVisibleLength(4);
		accrualYear.setValue(detail.getAccrualYear());
		accrualYear.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				detail.setAccrualYear(accrualYear.getValue());
				callback.onValueChanged(detail);
			}
		});
		tab2.setWidget(1, 4, accrualYear);
		panel.add(tab2);
		
		
		FlexTable tab3 = new FlexTable();
		tab3.getColumnFormatter().setWidth(0, "300px");
		tab3.getColumnFormatter().setWidth(1, "auto");
		
		tab3.setStyleName(AON.AON_CSS.aonWidthAll());
		tab3.addStyleName(AON.AON_CSS.aonMarginTop());
		tab3.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab3.getCellFormatter().setStyleName(0, 0, AON.AON_CSS.aonBorderBottom());
		tab3.getCellFormatter().addStyleName(0, 0, AON.AON_CSS.aonBold());
		tab3.getFlexCellFormatter().setColSpan(0, 0, 2);
		tab3.setWidget(0, 0, new InlineLabel(AON.MSG.buildingData()));

		tab3.setWidget(1, 0, new MediumLabel(AON.MSG.buildingLocation()));
		tab3.setWidget(1, 1, new MediumLabel(AON.MSG.cadasdralReference()));
		
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
		location.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				detail.setLocation(Integer.toString( location.getSelectedIndex()));
				callback.onValueChanged(detail);
			}
		});
		tab3.setWidget(2, 0, location);
		
		TextBox cadasdralReference = new TextBox();
		cadasdralReference.setVisibleLength(23);
		cadasdralReference.setMaxLength(20);
		cadasdralReference.setStyleName(AON.AON_CSS.aonInputText());
		cadasdralReference.setValue(detail.getCadasdralReference());
		cadasdralReference.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setCadasdralReference(cadasdralReference.getValue());
				callback.onValueChanged(detail);				
			}
		});
		tab3.setWidget(2, 1, cadasdralReference);
		panel.add(tab3);
		
		FlexTable tab4 = new FlexTable();
		tab4.setStyleName(AON.AON_CSS.aonWidthAll());
		tab4.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab4.getColumnFormatter().setWidth(0, "200px");
		tab4.getColumnFormatter().setWidth(1, "auto");
		
		tab4.setWidget(0, 0, new MediumLabel(AON.MSG.streetType()));
		tab4.setWidget(0, 1, new MediumLabel(AON.MSG.streetName()));

		StreetTypeListBox streetType = new StreetTypeListBox();
		streetType.setValue(StreetType.getForIneCode(detail.getStreetType()));
		streetType.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				detail.setStreetType(streetType.getValue(streetType.getSelectedIndex()));
				callback.onValueChanged(detail);
			}
		});
		tab4.setWidget(1, 0, streetType);
		
		TextBox streetName = new TextBox();
		streetName.setVisibleLength(23);
		streetName.setMaxLength(20);
		streetName.setStyleName(AON.AON_CSS.aonInputText());
		streetName.setValue(detail.getStreetName());
		streetName.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setStreetName(streetName.getValue());
				callback.onValueChanged(detail);				
			}
		});
		tab4.setWidget(1, 1, streetName);
		panel.add(tab4);
		
		FlexTable tab5 = new FlexTable();
		tab5.setStyleName(AON.AON_CSS.aonWidthAll());
		tab5.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab5.getColumnFormatter().setWidth(0, "70px");
		tab5.getColumnFormatter().setWidth(1, "60px");
		tab5.getColumnFormatter().setWidth(2, "60px");
		tab5.getColumnFormatter().setWidth(3, "60px");
		tab5.getColumnFormatter().setWidth(4, "60px");
		tab5.getColumnFormatter().setWidth(5, "60px");
		tab5.getColumnFormatter().setWidth(6, "60px");
		tab5.getColumnFormatter().setWidth(7, "60px");
		tab5.getColumnFormatter().setWidth(8, "auto");
		
		tab5.setWidget(0, 0, new MediumLabel(AON.MSG.streetNumberType()));
		tab5.setWidget(0, 1, new MediumLabel(AON.MSG.streetNumber()));
		tab5.setWidget(0, 2, new MediumLabel(AON.MSG.streetNumberSuffix()));
		tab5.setWidget(0, 3, new MediumLabel(AON.MSG.streetBlock()));
		tab5.setWidget(0, 4, new MediumLabel(AON.MSG.streetHall()));
		tab5.setWidget(0, 5, new MediumLabel(AON.MSG.streetStair()));
		tab5.setWidget(0, 6, new MediumLabel(AON.MSG.streetFloor()));
		tab5.setWidget(0, 7, new MediumLabel(AON.MSG.streetDoor()));
		tab5.setWidget(0, 8, new MediumLabel(AON.MSG.streetComplement()));

		TextBox numberType = new TextBox();
		numberType.setStyleName(AON.AON_CSS.aonInputText());
		numberType.setMaxLength(3);
		numberType.setVisibleLength(3);
		numberType.setValue(detail.getNumberType());
		numberType.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setNumberType(numberType.getValue());
				callback.onValueChanged(detail);				
			}
		});
		tab5.setWidget(1, 0, numberType);
		
		TextBox number = new TextBox();
		number.setStyleName(AON.AON_CSS.aonInputText());
		number.setMaxLength(5);
		number.setVisibleLength(5);
		number.setValue(detail.getNumber());
		number.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setNumber(number.getValue());
				callback.onValueChanged(detail);				
			}
		});
		tab5.setWidget(1, 1, number);
		
		TextBox numberSuffix = new TextBox();
		numberSuffix.setStyleName(AON.AON_CSS.aonInputText());
		numberSuffix.setMaxLength(3);
		numberSuffix.setVisibleLength(3);
		numberSuffix.setValue(detail.getNumberSuffix());
		numberSuffix.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setNumberSuffix(numberSuffix.getValue());
				callback.onValueChanged(detail);				
			}
		});
		tab5.setWidget(1, 2, numberSuffix);
		
		TextBox block = new TextBox();
		block.setStyleName(AON.AON_CSS.aonInputText());
		block.setMaxLength(3);
		block.setVisibleLength(3);
		block.setValue(detail.getBlock());
		block.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setBlock(block.getValue());
				callback.onValueChanged(detail);				
			}
		});
		tab5.setWidget(1, 3, block);
		
		TextBox hall = new TextBox();
		hall.setStyleName(AON.AON_CSS.aonInputText());
		hall.setMaxLength(3);
		hall.setVisibleLength(3);
		hall.setValue(detail.getHall());
		hall.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setHall(hall.getValue());
				callback.onValueChanged(detail);				
			}
		});
		tab5.setWidget(1, 4, hall);
		
		TextBox stair = new TextBox();
		stair.setStyleName(AON.AON_CSS.aonInputText());
		stair.setMaxLength(3);
		stair.setVisibleLength(3);
		stair.setValue(detail.getStair());
		stair.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setStair(stair.getValue());
				callback.onValueChanged(detail);				
			}
		});
		tab5.setWidget(1, 5, stair);
		
		TextBox floor = new TextBox();
		floor.setStyleName(AON.AON_CSS.aonInputText());
		floor.setMaxLength(3);
		floor.setVisibleLength(3);
		floor.setValue(detail.getFloor());
		floor.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setFloor(floor.getValue());
				callback.onValueChanged(detail);				
			}
		});
		tab5.setWidget(1, 6, floor);
		
		TextBox door = new TextBox();
		door.setStyleName(AON.AON_CSS.aonInputText());
		door.setMaxLength(3);
		door.setVisibleLength(3);
		door.setValue(detail.getDoor());
		door.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setDoor(door.getValue());
				callback.onValueChanged(detail);				
			}
		});
		tab5.setWidget(1, 7, door);
		
		TextBox complement = new TextBox();
		complement.setStyleName(AON.AON_CSS.aonInputText());
		complement.setMaxLength(40);
		complement.setVisibleLength(20);
		complement.setValue(detail.getComplement());
		complement.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setComplement(complement.getValue());
				callback.onValueChanged(detail);				
			}
		});
		tab5.setWidget(1, 8, complement);
		
		panel.add(tab5);
		
		FlexTable tab6 = new FlexTable();
		tab6.setStyleName(AON.AON_CSS.aonWidthAll());
		tab6.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab6.getColumnFormatter().setWidth(0, "150px");
		tab6.getColumnFormatter().setWidth(1, "150px");
		tab6.getColumnFormatter().setWidth(2, "100px");
		tab6.getColumnFormatter().setWidth(3, "150px");
		tab6.getColumnFormatter().setWidth(4, "auto");
		
		tab6.setWidget(0, 0, new MediumLabel(AON.MSG.city()));
		tab6.setWidget(0, 1, new MediumLabel(AON.MSG.town()));
		tab6.setWidget(0, 2, new MediumLabel(AON.MSG.townCode()));
		tab6.setWidget(0, 3, new MediumLabel(AON.MSG.province()));
		tab6.setWidget(0, 4, new MediumLabel(AON.MSG.zip()));
		
		TextBox city = new TextBox();
		city.setStyleName(AON.AON_CSS.aonInputText());
		city.setMaxLength(20);
		city.setVisibleLength(20);
		city.setValue(detail.getCity());
		city.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setCity(city.getValue());
				callback.onValueChanged(detail);				
			}
		});
		tab6.setWidget(1, 0, city);

		TextBox town = new TextBox();
		town.setStyleName(AON.AON_CSS.aonInputText());
		town.setMaxLength(30);
		town.setVisibleLength(20);
		town.setValue(detail.getTown());
		town.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setTown(town.getValue());
				callback.onValueChanged(detail);				
			}
		});
		tab6.setWidget(1, 1, town);

		FlowPanel townCodePanel = new FlowPanel(); 
		TextBox townCode = new TextBox();
		townCode.setStyleName(AON.AON_CSS.aonInputText());
		townCode.setMaxLength(5);
		townCode.setVisibleLength(5);
		townCode.setValue(detail.getTownCode());
		townCode.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setTownCode(townCode.getValue());
				callback.onValueChanged(detail);				
			}
		});
		townCodePanel.add(townCode);
		
		Anchor townCodeAnchor = new Anchor();
		townCodeAnchor.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		townCodeAnchor.addStyleName(AON.AON_CSS.aonIconGoto());
		townCodeAnchor.setTarget("_blank");
		townCodeAnchor.setHref("http://www.ine.es/daco/daco42/codmun/codmunmapa.htm");
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
		provinceCode.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				detail.setProvinceCode(Integer.toString(provinceCode.getSelectedIndex()));
				callback.onValueChanged(detail);
			}
		});
		tab6.setWidget(1, 3, provinceCode);

		TextBox zip = new TextBox();
		zip.setStyleName(AON.AON_CSS.aonInputText());
		zip.setMaxLength(5);
		zip.setVisibleLength(5);
		zip.setValue(detail.getZip());
		zip.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				detail.setZip(zip.getValue());
				callback.onValueChanged(detail);				
			}
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
