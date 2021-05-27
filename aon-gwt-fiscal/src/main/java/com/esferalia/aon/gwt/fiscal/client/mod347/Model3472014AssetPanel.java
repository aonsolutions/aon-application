package com.esferalia.aon.gwt.fiscal.client.mod347;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.StreetTypeListBox;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347Asset2014.IModel347AssetCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Asset;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class Model3472014AssetPanel extends SimpleLayoutPanel implements Focusable {
	
	private static class MediumLabel extends InlineLabel {
		private MediumLabel(String label) {
			super();
			if (AonStringUtils.length(label) > 35) {
				setText(AonStringUtils.abbreviate(label, 35));
				setTitle(label);
			} else {
				setText(label);
			}
			setStyleName(AON.AON_CSS.aonFontMedium());
		}
	}
	private int tabIndex; 

	private DocumentTextBox document = new DocumentTextBox();	

	public Model3472014AssetPanel(Mod347Asset asset, IModel347AssetCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		// NIF Arrendatario / NIF Representante / Nombre
		
		FlexTable tab1 = new FlexTable();
		tab1.getColumnFormatter().setWidth(0, "100px");
		tab1.getColumnFormatter().setWidth(1, "100px");
		tab1.getColumnFormatter().setWidth(2, "auto");
		
		tab1.setStyleName(AON.AON_CSS.aonWidthAll());
		tab1.addStyleName(AON.AON_CSS.aonNowrap());

		tab1.setWidget(0, 0, new MediumLabel("NIF arrendatario"));
		tab1.setWidget(0, 1, new MediumLabel(AON.MSG.representativeDocument()));
		tab1.setWidget(0, 2, new MediumLabel(AON.MSG.fullName()));
		
		document.setValue(asset.getDocument());
		document.setMaxLength(9);
		document.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setDocument(document.getValue());
				callback.onValueChanged(asset);
			}
		});
		tab1.setWidget(1, 0, document);
		
		DocumentTextBox representativeDocument = new DocumentTextBox();
		representativeDocument.setValue(asset.getRepresentativeDocument());
		representativeDocument.setMaxLength(9);
		representativeDocument.addValueChangeHandler( new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setRepresentativeDocument(representativeDocument.getValue());
				callback.onValueChanged(asset);
			}
		});
		tab1.setWidget(1, 1, representativeDocument);
				
		TextBox name = new TextBox();
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.setStyleName(AON.AON_CSS.aonInputText());		
		name.setValue(asset.getName());
		name.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setName(name.getValue());
				callback.onTableChanged(asset);
			}
		});
		tab1.setWidget(1, 2, name);
		
		panel.add(tab1);
		
		// Importe de la operación / Referencia Catastral / Situación
		
		FlexTable tab2 = new FlexTable();
		tab2.getColumnFormatter().setWidth(0, "130px");
		tab2.getColumnFormatter().setWidth(1, "170px");
		tab2.getColumnFormatter().setWidth(2, "auto");
		
		tab2.setStyleName(AON.AON_CSS.aonWidthAll());
		tab2.addStyleName(AON.AON_CSS.aonNowrap());

		tab2.setWidget(0, 0, new MediumLabel(AON.MSG.amount()));
		tab2.setWidget(0, 1, new MediumLabel(AON.MSG.cadasdralReference()));
		tab2.setWidget(0, 2, new MediumLabel(AON.MSG.buildingLocation()));	

		DoubleBox amount = new DoubleBox();
		amount.setValue(asset.getAmount());
		amount.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {				
				asset.setAmount(amount.getValue());
				callback.onValueChanged(asset);
			}
		});
		tab2.setWidget(1, 0, amount);
		
		TextBox cadasdralReference = new TextBox();
		cadasdralReference.setVisibleLength(25);
		cadasdralReference.setMaxLength(25);
		cadasdralReference.setStyleName(AON.AON_CSS.aonInputText());
		cadasdralReference.setValue(asset.getCadasdralReference());
		cadasdralReference.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setCadasdralReference(cadasdralReference.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab2.setWidget(1, 1, cadasdralReference);	
		
		// Situacion del inmueble
		// 1. Inmueble con referencia catastral situado en cualquier punto del territorio español, excepto País Vasco y Navarra.
		// 2. Inmueble situado en la Comunidad Autónoma del País Vasco o en la Comunidad Foral de Navarra.
		// 3. Inmueble en cualquiera de las situaciones anteriores pero sin referencia catastral.
		// 4. Inmueble situado en el extranjero. 
		
		ListBox location = new ListBox();
		location.addItem( AON.MSG.buildingLocationValue(0) );
		location.addItem( "Inmueble con referencia catastral situado en territorio espa\u00F1ol, excepto Pa\u00EDs Vasco y Navarra" );	
		location.addItem( "Inmueble situado en la Comunidad Aut\u00F3noma del Pa\u00EDs Vasco o en la Comunidad Foral de Navarra" );
		location.addItem( "Inmueble situado en territorio espa\u00F1ol, pero sin referencia catastral" );
		location.addItem( "Inmueble situado en el extranjero" );
		
		location.setSelectedIndex( 0 );
		if (AonStringUtils.isNotBlank(asset.getAssetLocation())) {
			try {
				location.setSelectedIndex( Integer.parseInt(asset.getAssetLocation()) );
			} catch (NumberFormatException e) { 
				// nothing 
			}
		}
		location.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				asset.setAssetLocation(Integer.toString( location.getSelectedIndex()));
				callback.onValueChanged(asset);
			}
		});
		tab2.setWidget(1, 2, location);
		
		panel.add(tab2);
		
		// Tipo via / Nombre via / Tipo Num / Numero / Calif. Num / Bloque / Portal / Escalera / Piso / Puerta
		
		FlexTable tab3 = new FlexTable();
		tab3.setStyleName(AON.AON_CSS.aonWidthAll());
		tab3.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab3.getColumnFormatter().setWidth(0, "110px");
		tab3.getColumnFormatter().setWidth(1, "250px");		
		tab3.getColumnFormatter().setWidth(2, "50px");
		tab3.getColumnFormatter().setWidth(3, "65px");
		tab3.getColumnFormatter().setWidth(4, "50px");
		tab3.getColumnFormatter().setWidth(5, "50px");
		tab3.getColumnFormatter().setWidth(6, "50px");
		tab3.getColumnFormatter().setWidth(7, "50px");
		tab3.getColumnFormatter().setWidth(8, "50px");
		tab3.getColumnFormatter().setWidth(9, "auto");		

		tab3.setWidget(0, 0, new MediumLabel(AON.MSG.streetType()));
		tab3.setWidget(0, 1, new MediumLabel(AON.MSG.streetName()));
		tab3.setWidget(0, 2, new MediumLabel("Tipo N\u00FAm."));
		tab3.setWidget(0, 3, new MediumLabel(AON.MSG.streetNumber()));
		tab3.setWidget(0, 4, new MediumLabel(AON.MSG.streetNumberSuffix()));
		tab3.setWidget(0, 5, new MediumLabel(AON.MSG.streetBlock()));
		tab3.setWidget(0, 6, new MediumLabel(AON.MSG.streetHall()));
		tab3.setWidget(0, 7, new MediumLabel(AON.MSG.streetStair()));
		tab3.setWidget(0, 8, new MediumLabel(AON.MSG.streetFloor()));
		tab3.setWidget(0, 9, new MediumLabel(AON.MSG.streetDoor()));		

		StreetTypeListBox streetType = new StreetTypeListBox();
		streetType.setValue(StreetType.getForIneCode(asset.getAssetStreetType()));
		streetType.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				asset.setAssetStreetType(streetType.getValue(streetType.getSelectedIndex()));
				callback.onValueChanged(asset);
			}
		});
		tab3.setWidget(1, 0, streetType);
		
		TextBox streetName = new TextBox();
		streetName.setVisibleLength(50);
		streetName.setMaxLength(50);
		streetName.setStyleName(AON.AON_CSS.aonInputText());
		streetName.setValue(asset.getAssetStreet());
		streetName.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreet(streetName.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab3.setWidget(1, 1, streetName);
		
		TextBox numberType = new TextBox();
		numberType.setStyleName(AON.AON_CSS.aonInputText());
		numberType.setMaxLength(3);
		numberType.setVisibleLength(3);
		numberType.setValue(asset.getAssetStreetNumberType());
		numberType.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetNumberType(numberType.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab3.setWidget(1, 2, numberType);
		
		TextBox number = new TextBox();
		number.setStyleName(AON.AON_CSS.aonInputText());
		number.setMaxLength(5);
		number.setVisibleLength(5);
		number.setValue(asset.getAssetStreetNumber());
		number.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetNumber(number.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab3.setWidget(1, 3, number);
		
		TextBox numberSuffix = new TextBox();
		numberSuffix.setStyleName(AON.AON_CSS.aonInputText());
		numberSuffix.setMaxLength(3);
		numberSuffix.setVisibleLength(3);
		numberSuffix.setValue(asset.getAssetStreetNumberSuffix());
		numberSuffix.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetNumberSuffix(numberSuffix.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab3.setWidget(1, 4, numberSuffix);
		
		TextBox block = new TextBox();
		block.setStyleName(AON.AON_CSS.aonInputText());
		block.setMaxLength(3);
		block.setVisibleLength(3);
		block.setValue(asset.getAssetStreetBlock());
		block.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetBlock(block.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab3.setWidget(1, 5, block);
		
		TextBox hall = new TextBox();
		hall.setStyleName(AON.AON_CSS.aonInputText());
		hall.setMaxLength(3);
		hall.setVisibleLength(3);
		hall.setValue(asset.getAssetStreetHall());
		hall.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetHall(hall.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab3.setWidget(1, 6, hall);
		
		TextBox stair = new TextBox();
		stair.setStyleName(AON.AON_CSS.aonInputText());
		stair.setMaxLength(3);
		stair.setVisibleLength(3);
		stair.setValue(asset.getAssetStreetStair());
		stair.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetStair(stair.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab3.setWidget(1, 7, stair);
		
		TextBox floor = new TextBox();
		floor.setStyleName(AON.AON_CSS.aonInputText());
		floor.setMaxLength(3);
		floor.setVisibleLength(3);
		floor.setValue(asset.getAssetStreetFloor());
		floor.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetFloor(floor.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab3.setWidget(1, 8, floor);
		
		TextBox door = new TextBox();
		door.setStyleName(AON.AON_CSS.aonInputText());
		door.setMaxLength(3);
		door.setVisibleLength(3);
		door.setValue(asset.getAssetStreetDoor());
		door.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetDoor(door.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab3.setWidget(1, 9, door);
		
		panel.add(tab3);
		
		// Complemento / Localidad		
		
		FlexTable tab4 = new FlexTable();
		tab4.setStyleName(AON.AON_CSS.aonWidthAll());
		tab4.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab4.getColumnFormatter().setWidth(0, "300px");
		tab4.getColumnFormatter().setWidth(1, "auto");		
		
		tab4.setWidget(0, 0, new MediumLabel(AON.MSG.streetComplement()));
		tab4.setWidget(0, 1, new MediumLabel(AON.MSG.city()));
		
		TextBox complement = new TextBox();
		complement.setStyleName(AON.AON_CSS.aonInputText());
		complement.setMaxLength(40);
		complement.setVisibleLength(40);
		complement.setValue(asset.getAssetStreetComplement());
		complement.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetComplement(complement.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab4.setWidget(1, 0, complement);
		
		TextBox city = new TextBox();
		city.setStyleName(AON.AON_CSS.aonInputText());
		city.setMaxLength(30);
		city.setVisibleLength(30);
		city.setValue(asset.getAssetStreetCity());
		city.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetCity(city.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab4.setWidget(1, 1, city);
		
		panel.add(tab4);
		
		// Municipio / Código municipio / Provincia / Código postal
		
		FlexTable tab5 = new FlexTable();
		tab5.setStyleName(AON.AON_CSS.aonWidthAll());
		tab5.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab5.getColumnFormatter().setWidth(0, "250px");
		tab5.getColumnFormatter().setWidth(1, "120px");
		tab5.getColumnFormatter().setWidth(2, "150px");
		tab5.getColumnFormatter().setWidth(3, "auto");		
		
		tab5.setWidget(0, 0, new MediumLabel(AON.MSG.town()));
		tab5.setWidget(0, 1, new MediumLabel(AON.MSG.townCode()));
		tab5.setWidget(0, 2, new MediumLabel(AON.MSG.province()));
		tab5.setWidget(0, 3, new MediumLabel(AON.MSG.zip()));

		TextBox town = new TextBox();
		town.setStyleName(AON.AON_CSS.aonInputText());
		town.setMaxLength(30);
		town.setVisibleLength(30);
		town.setValue(asset.getAssetStreetTown());
		town.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetTown(town.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab5.setWidget(1, 0, town);

		FlowPanel townCodePanel = new FlowPanel(); 
		TextBox townCode = new TextBox();
		townCode.setStyleName(AON.AON_CSS.aonInputText());
		townCode.setMaxLength(5);
		townCode.setVisibleLength(5);
		townCode.setValue(asset.getAssetStreetTownCode());
		townCode.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetTownCode(townCode.getValue());
				callback.onValueChanged(asset);				
			}
		});
		townCodePanel.add(townCode);
		
		Anchor townCodeAnchor = new Anchor();
		townCodeAnchor.setStyleName(AON.AON_CSS.aonIconPaddingLeft());
		townCodeAnchor.addStyleName(AON.AON_CSS.aonIconGoto());
		townCodeAnchor.setTarget("_blank");
		townCodeAnchor.setHref("http://www.ine.es/daco/daco42/codmun/codmunmapa.htm");
		townCodePanel.add(townCodeAnchor);
		
		tab5.setWidget(1, 1, townCodePanel);
		
		ProvinceListBox provinceCode = new ProvinceListBox();
		if (AonStringUtils.isNotBlank(asset.getAssetStreetProvince())) {
			try {
				provinceCode.setSelectedIndex( Integer.parseInt(asset.getAssetStreetProvince()) );
			} catch (NumberFormatException e) {
				provinceCode.setSelectedIndex( 0 );
			}
		} else {
			provinceCode.setSelectedIndex( 0 );
		}
		provinceCode.addChangeHandler( new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				asset.setAssetStreetProvince(Integer.toString(provinceCode.getSelectedIndex()));
				callback.onValueChanged(asset);
			}
		});
		tab5.setWidget(1, 2, provinceCode);

		TextBox zip = new TextBox();
		zip.setStyleName(AON.AON_CSS.aonInputText());
		zip.setMaxLength(5);
		zip.setVisibleLength(5);
		zip.setValue(asset.getAssetStreetZip());
		zip.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetZip(zip.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab5.setWidget(1, 3, zip);
		panel.add(tab5);		
		
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
		document.setFocus(true);
	}

	@Override
	public void setTabIndex(int index) {
		tabIndex = index;
	}	

}
