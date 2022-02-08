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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;

public class Model3472014AssetPanelGipuzkoa extends SimpleLayoutPanel implements Focusable {
	
	private int tabIndex; 
	private AonDocumentTextBox document = new AonDocumentTextBox();	

	public Model3472014AssetPanelGipuzkoa(Mod347Asset asset, IModel347AssetCallback callback) {
		ScrollPanel scroll = new ScrollPanel();
		scroll.setStyleName(AON.AON_CSS.aonWidthAll());
		FlowPanel panel = new FlowPanel();
		panel.setStyleName(AON.AON_CSS.aonScrollArea());
		
		// NIF Arrendatario / Nombre
		
		FlexTable tab1 = new FlexTable();
		tab1.getColumnFormatter().setWidth(0, "100px");		
		tab1.getColumnFormatter().setWidth(1, "auto");
		
		tab1.setStyleName(AON.AON_CSS.aonWidthAll());
		tab1.addStyleName(AON.AON_CSS.aonNowrap());

		tab1.setWidget(0, 0, new Model347SmallerLabel("NIF arrendatario"));		
		tab1.setWidget(0, 1, new Model347SmallerLabel(AON.MSG.fullName()));
		
		document.setValue(asset.getDocument());
		document.setMaxLength(9);
		document.addValueChangeHandler( event -> {
			asset.setDocument(document.getValue());
			callback.onValueChanged(asset);
		});
		tab1.setWidget(1, 0, document);
		
		AonTextBox name = new AonTextBox();
		name.setVisibleLength(40);
		name.setMaxLength(40);
		name.setValue(asset.getName());
		name.addValueChangeHandler(event -> {
			asset.setName(name.getValue());
			callback.onTableChanged(asset);
		});
		tab1.setWidget(1, 1, name);
		
		panel.add(tab1);
		
		// Importe de la operación / Referencia Catastral / Provincia
		
		FlexTable tab2 = new FlexTable();
		tab2.getColumnFormatter().setWidth(0, "130px");
		tab2.getColumnFormatter().setWidth(1, "190px");
		tab2.getColumnFormatter().setWidth(2, "auto");
		
		tab2.setStyleName(AON.AON_CSS.aonWidthAll());
		tab2.addStyleName(AON.AON_CSS.aonNowrap());

		tab2.setWidget(0, 0, new Model347SmallerLabel(AON.MSG.amount()));
		tab2.setWidget(0, 1, new Model347SmallerLabel(AON.MSG.cadasdralReference()));		
		tab2.setWidget(0, 2, new Model347SmallerLabel(AON.MSG.province()));	

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
		tab2.setWidget(1, 2, provinceCode);
		
		panel.add(tab2);
		
		// Municipio / Siglas / Nombre via / Numero / Escalera / Piso / Puerta
		
		FlexTable tab3 = new FlexTable();
		tab3.setStyleName(AON.AON_CSS.aonWidthAll());
		tab3.addStyleName(AON.AON_CSS.aonNowrap());
		
		tab3.getColumnFormatter().setWidth(0, "120px");
		tab3.getColumnFormatter().setWidth(1, "100px");		
		tab3.getColumnFormatter().setWidth(2, "130px");
		tab3.getColumnFormatter().setWidth(3, "50px");
		tab3.getColumnFormatter().setWidth(4, "30px");
		tab3.getColumnFormatter().setWidth(5, "30px");
		tab3.getColumnFormatter().setWidth(6, "auto");		

		tab3.setWidget(0, 0, new Model347SmallerLabel(AON.MSG.town()));
		tab3.setWidget(0, 1, new Model347SmallerLabel(AON.MSG.streetType()));
		tab3.setWidget(0, 2, new Model347SmallerLabel(AON.MSG.streetName()));		
		tab3.setWidget(0, 3, new Model347SmallerLabel(AON.MSG.streetNumber()));
		tab3.setWidget(0, 4, new Model347SmallerLabel(AON.MSG.streetStair()));
		tab3.setWidget(0, 5, new Model347SmallerLabel(AON.MSG.streetFloor()));
		tab3.setWidget(0, 6, new Model347SmallerLabel(AON.MSG.streetDoor()));
		
		AonTextBox town = new AonTextBox();
		town.setMaxLength(24);
		town.setVisibleLength(24);
		town.setValue(asset.getAssetStreetTown());
		town.addValueChangeHandler(event -> {
			asset.setAssetStreetTown(town.getValue());
			callback.onValueChanged(asset);				
		});
		tab3.setWidget(1, 0, town);

		// Se graba según INE (hasta 5 caracteres), aunque luego el fichero lleva 2 caracteres (según AEAT)
		StreetTypeListBox streetType = new StreetTypeListBox();
		streetType.setValue(StreetType.getForIneCode(asset.getAssetStreetType()));
		streetType.addChangeHandler( event -> {
			asset.setAssetStreetType(streetType.getValue(streetType.getSelectedIndex()));
			callback.onValueChanged(asset);
		});
		tab3.setWidget(1, 1, streetType);
		
		AonTextBox streetName = new AonTextBox();
		streetName.setVisibleLength(25);
		streetName.setMaxLength(25);
		streetName.setValue(asset.getAssetStreet());
		streetName.addValueChangeHandler(event -> {
			asset.setAssetStreet(streetName.getValue());
			callback.onValueChanged(asset);				
		});
		tab3.setWidget(1, 2, streetName);
		
		AonTextBox number = new AonTextBox();
		number.setMaxLength(5);
		number.setVisibleLength(5);
		number.setValue(asset.getAssetStreetNumber());
		number.addValueChangeHandler(event -> {
			asset.setAssetStreetNumber(number.getValue());
			callback.onValueChanged(asset);				
		});
		tab3.setWidget(1, 3, number);
		
		AonTextBox stair = new AonTextBox();
		stair.setMaxLength(2);
		stair.setVisibleLength(2);
		stair.setValue(asset.getAssetStreetStair());
		stair.addValueChangeHandler(event -> {
			asset.setAssetStreetStair(stair.getValue());
			callback.onValueChanged(asset);				
		});
		tab3.setWidget(1, 4, stair);
		
		AonTextBox floor = new AonTextBox();
		floor.setMaxLength(2);
		floor.setVisibleLength(2);
		floor.setValue(asset.getAssetStreetFloor());
		floor.addValueChangeHandler(event -> {
			asset.setAssetStreetFloor(floor.getValue());
			callback.onValueChanged(asset);				
		});
		tab3.setWidget(1, 5, floor);
		
		AonTextBox door = new AonTextBox();
		door.setMaxLength(2);
		door.setVisibleLength(2);
		door.setValue(asset.getAssetStreetDoor());
		door.addValueChangeHandler(event -> {
			asset.setAssetStreetDoor(door.getValue());
			callback.onValueChanged(asset);				
		});
		tab3.setWidget(1, 6, door);
		
		panel.add(tab3);
		
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
