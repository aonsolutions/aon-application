package com.esferalia.aon.gwt.fiscal.client.mod347;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.ProvinceListBox;
import com.esferalia.aon.gwt.common.client.widget.StreetTypeListBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDocumentTextBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonDoubleBox;
import com.esferalia.aon.gwt.common.client.widget.solutions.AonTextBox;
import com.esferalia.aon.gwt.fiscal.client.mod347.Model347Asset2014.IModel347AssetCallback;
import com.esferalia.aon.occam.api.model.fiscal.Mod347Asset;
import com.esferalia.aon.occam.api.model.type.StreetType;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model3472014AssetPanel extends SimpleLayoutPanel implements Focusable {
	
	private int tabIndex; 

	private AonDocumentTextBox document = new AonDocumentTextBox();	

	public Model3472014AssetPanel(Mod347Asset asset, IModel347AssetCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.CSS.aonWidthAll());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.CSS.aonScrollArea());
		
		// NIF Arrendatario / NIF Representante / Nombre
		
		FlexTable tab1 = new FlexTable();
		tab1.getColumnFormatter().setWidth(0, "100px");
		tab1.getColumnFormatter().setWidth(1, "100px");
		tab1.getColumnFormatter().setWidth(2, "auto");
		
		tab1.setStyleName(AON.CSS.aonWidthAll());
		tab1.addStyleName(AON.CSS.aonNowrap());

		tab1.setWidget(0, 0, new Model347SmallerLabel("NIF arrendatario"));
		tab1.setWidget(0, 1, new Model347SmallerLabel(AON.MSG.representativeDocument()));
		tab1.setWidget(0, 2, new Model347SmallerLabel(AON.MSG.fullName()));
		
		document.setValue(asset.getDocument());
		document.setMaxLength(9);
		document.addValueChangeHandler( event -> {
			asset.setDocument(document.getValue());
			callback.onValueChanged(asset);
		});
		tab1.setWidget(1, 0, document);
		
		AonDocumentTextBox representativeDocument = new AonDocumentTextBox();
		representativeDocument.setValue(asset.getRepresentativeDocument());
		representativeDocument.setMaxLength(9);
		representativeDocument.addValueChangeHandler( event -> {
			asset.setRepresentativeDocument(representativeDocument.getValue());
			callback.onValueChanged(asset);
		});
		tab1.setWidget(1, 1, representativeDocument);
				
		AonTextBox name = new AonTextBox();
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.setValue(asset.getName());
		name.addValueChangeHandler(event -> {
			asset.setName(name.getValue());
			callback.onTableChanged(asset);
		});
		tab1.setWidget(1, 2, name);
		
		panel.add(tab1);
		
		// Importe de la operación / Referencia Catastral / Situación
		
		FlexTable tab2 = new FlexTable();
		tab2.getColumnFormatter().setWidth(0, "130px");
		tab2.getColumnFormatter().setWidth(1, "170px");
		tab2.getColumnFormatter().setWidth(2, "auto");
		
		tab2.setStyleName(AON.CSS.aonWidthAll());
		tab2.addStyleName(AON.CSS.aonNowrap());

		tab2.setWidget(0, 0, new Model347SmallerLabel(AON.MSG.amount()));
		tab2.setWidget(0, 1, new Model347SmallerLabel(AON.MSG.cadasdralReference()));
		tab2.setWidget(0, 2, new Model347SmallerLabel(AON.MSG.buildingLocation()));	

		AonDoubleBox amount = new AonDoubleBox();
		amount.setValue(asset.getAmount());
		amount.addValueChangeHandler(event -> {				
			asset.setAmount(amount.getValue());
			callback.onValueChanged(asset);
		});
		tab2.setWidget(1, 0, amount);
		
		AonTextBox cadasdralReference = new AonTextBox();
		cadasdralReference.setVisibleLength(25);
		cadasdralReference.setMaxLength(25);
		cadasdralReference.setValue(asset.getCadasdralReference());
		cadasdralReference.addValueChangeHandler(event -> {
			asset.setCadasdralReference(cadasdralReference.getValue());
			callback.onValueChanged(asset);				
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
		location.addChangeHandler(event -> {
			asset.setAssetLocation(Integer.toString( location.getSelectedIndex()));
			callback.onValueChanged(asset);
		});
		tab2.setWidget(1, 2, location);
		
		panel.add(tab2);
		
		// Tipo via / Nombre via / Tipo Num / Numero / Calif. Num / Bloque / Portal / Escalera / Piso / Puerta
		
		FlexTable tab3 = new FlexTable();
		tab3.setStyleName(AON.CSS.aonWidthAll());
		tab3.addStyleName(AON.CSS.aonNowrap());
		
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

		tab3.setWidget(0, 0, new Model347SmallerLabel(AON.MSG.streetType()));
		tab3.setWidget(0, 1, new Model347SmallerLabel(AON.MSG.streetName()));
		tab3.setWidget(0, 2, new Model347SmallerLabel("Tipo N\u00FAm."));
		tab3.setWidget(0, 3, new Model347SmallerLabel(AON.MSG.streetNumber()));
		tab3.setWidget(0, 4, new Model347SmallerLabel(AON.MSG.streetNumberSuffix()));
		tab3.setWidget(0, 5, new Model347SmallerLabel(AON.MSG.streetBlock()));
		tab3.setWidget(0, 6, new Model347SmallerLabel(AON.MSG.streetHall()));
		tab3.setWidget(0, 7, new Model347SmallerLabel(AON.MSG.streetStair()));
		tab3.setWidget(0, 8, new Model347SmallerLabel(AON.MSG.streetFloor()));
		tab3.setWidget(0, 9, new Model347SmallerLabel(AON.MSG.streetDoor()));		

		StreetTypeListBox streetType = new StreetTypeListBox();
		streetType.setValue(StreetType.getForIneCode(asset.getAssetStreetType()));
		streetType.addChangeHandler( event -> {
			asset.setAssetStreetType(streetType.getValue(streetType.getSelectedIndex()));
			callback.onValueChanged(asset);
		});
		tab3.setWidget(1, 0, streetType);
		
		AonTextBox streetName = new AonTextBox();
		streetName.setVisibleLength(50);
		streetName.setMaxLength(50);
		streetName.setValue(asset.getAssetStreet());
		streetName.addValueChangeHandler(event -> {
			asset.setAssetStreet(streetName.getValue());
			callback.onValueChanged(asset);				
		});
		tab3.setWidget(1, 1, streetName);
		
		AonTextBox numberType = new AonTextBox();
		numberType.setMaxLength(3);
		numberType.setVisibleLength(3);
		numberType.setValue(asset.getAssetStreetNumberType());
		numberType.addValueChangeHandler(event -> {
			asset.setAssetStreetNumberType(numberType.getValue());
			callback.onValueChanged(asset);				
		});
		tab3.setWidget(1, 2, numberType);
		
		AonTextBox number = new AonTextBox();
		number.setMaxLength(5);
		number.setVisibleLength(5);
		number.setValue(asset.getAssetStreetNumber());
		number.addValueChangeHandler(event -> {
			asset.setAssetStreetNumber(number.getValue());
			callback.onValueChanged(asset);				
		});
		tab3.setWidget(1, 3, number);
		
		AonTextBox numberSuffix = new AonTextBox();
		numberSuffix.setMaxLength(3);
		numberSuffix.setVisibleLength(3);
		numberSuffix.setValue(asset.getAssetStreetNumberSuffix());
		numberSuffix.addValueChangeHandler(event -> {
			asset.setAssetStreetNumberSuffix(numberSuffix.getValue());
			callback.onValueChanged(asset);				
		});
		tab3.setWidget(1, 4, numberSuffix);
		
		AonTextBox block = new AonTextBox();
		block.setMaxLength(3);
		block.setVisibleLength(3);
		block.setValue(asset.getAssetStreetBlock());
		block.addValueChangeHandler(event -> {
			asset.setAssetStreetBlock(block.getValue());
			callback.onValueChanged(asset);				
		});
		tab3.setWidget(1, 5, block);
		
		AonTextBox hall = new AonTextBox();
		hall.setMaxLength(3);
		hall.setVisibleLength(3);
		hall.setValue(asset.getAssetStreetHall());
		hall.addValueChangeHandler(event -> {
			asset.setAssetStreetHall(hall.getValue());
			callback.onValueChanged(asset);				
		});
		tab3.setWidget(1, 6, hall);
		
		AonTextBox stair = new AonTextBox();
		stair.setMaxLength(3);
		stair.setVisibleLength(3);
		stair.setValue(asset.getAssetStreetStair());
		stair.addValueChangeHandler(event -> {
			asset.setAssetStreetStair(stair.getValue());
			callback.onValueChanged(asset);				
		});
		tab3.setWidget(1, 7, stair);
		
		AonTextBox floor = new AonTextBox();
		floor.setMaxLength(3);
		floor.setVisibleLength(3);
		floor.setValue(asset.getAssetStreetFloor());
		floor.addValueChangeHandler(event -> {
			asset.setAssetStreetFloor(floor.getValue());
			callback.onValueChanged(asset);				
		});
		tab3.setWidget(1, 8, floor);
		
		AonTextBox door = new AonTextBox();
		door.setMaxLength(3);
		door.setVisibleLength(3);
		door.setValue(asset.getAssetStreetDoor());
		door.addValueChangeHandler(event -> {
			asset.setAssetStreetDoor(door.getValue());
			callback.onValueChanged(asset);				
		});
		tab3.setWidget(1, 9, door);
		
		panel.add(tab3);
		
		// Complemento / Localidad		
		
		FlexTable tab4 = new FlexTable();
		tab4.setStyleName(AON.CSS.aonWidthAll());
		tab4.addStyleName(AON.CSS.aonNowrap());
		
		tab4.getColumnFormatter().setWidth(0, "300px");
		tab4.getColumnFormatter().setWidth(1, "auto");		
		
		tab4.setWidget(0, 0, new Model347SmallerLabel(AON.MSG.streetComplement()));
		tab4.setWidget(0, 1, new Model347SmallerLabel(AON.MSG.city()));
		
		AonTextBox complement = new AonTextBox();
		complement.setMaxLength(40);
		complement.setVisibleLength(40);
		complement.setValue(asset.getAssetStreetComplement());
		complement.addValueChangeHandler(event -> {
			asset.setAssetStreetComplement(complement.getValue());
			callback.onValueChanged(asset);				
		});
		tab4.setWidget(1, 0, complement);
		
		AonTextBox city = new AonTextBox();
		city.setMaxLength(30);
		city.setVisibleLength(30);
		city.setValue(asset.getAssetStreetCity());
		city.addValueChangeHandler(event -> {
			asset.setAssetStreetCity(city.getValue());
			callback.onValueChanged(asset);				
		});
		tab4.setWidget(1, 1, city);
		
		panel.add(tab4);
		
		// Municipio / Código municipio / Provincia / Código postal
		
		FlexTable tab5 = new FlexTable();
		tab5.setStyleName(AON.CSS.aonWidthAll());
		tab5.addStyleName(AON.CSS.aonNowrap());
		
		tab5.getColumnFormatter().setWidth(0, "250px");
		tab5.getColumnFormatter().setWidth(1, "120px");
		tab5.getColumnFormatter().setWidth(2, "150px");
		tab5.getColumnFormatter().setWidth(3, "auto");		
		
		tab5.setWidget(0, 0, new Model347SmallerLabel(AON.MSG.town()));
		tab5.setWidget(0, 1, new Model347SmallerLabel(AON.MSG.townCode()));
		tab5.setWidget(0, 2, new Model347SmallerLabel(AON.MSG.province()));
		tab5.setWidget(0, 3, new Model347SmallerLabel(AON.MSG.zip()));

		AonTextBox town = new AonTextBox();
		town.setMaxLength(30);
		town.setVisibleLength(30);
		town.setValue(asset.getAssetStreetTown());
		town.addValueChangeHandler(event -> {
			asset.setAssetStreetTown(town.getValue());
			callback.onValueChanged(asset);				
		});
		tab5.setWidget(1, 0, town);

		FlowPanel townCodePanel = new FlowPanel(); 
		AonTextBox townCode = new AonTextBox();
		townCode.setMaxLength(5);
		townCode.setVisibleLength(5);
		townCode.setValue(asset.getAssetStreetTownCode());
		townCode.addValueChangeHandler(event -> {
			asset.setAssetStreetTownCode(townCode.getValue());
			callback.onValueChanged(asset);				
		});
		townCodePanel.add(townCode);
		
		Anchor townCodeAnchor = new Anchor();
		townCodeAnchor.setStyleName(AON.CSS.aonIconLabel());
		townCodeAnchor.addStyleName(AON.CSS.aonIconLink());
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
		provinceCode.addChangeHandler( event -> {
			asset.setAssetStreetProvince(Integer.toString(provinceCode.getSelectedIndex()));
			callback.onValueChanged(asset);
		});
		tab5.setWidget(1, 2, provinceCode);

		AonTextBox zip = new AonTextBox();
		zip.setMaxLength(5);
		zip.setVisibleLength(5);
		zip.setValue(asset.getAssetStreetZip());
		zip.addValueChangeHandler(event -> {
			asset.setAssetStreetZip(zip.getValue());
			callback.onValueChanged(asset);				
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
		// Nothing
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
