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
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;

public class Model3472014AssetPanelGipuzkoa extends SimpleLayoutPanel implements Focusable {
	
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

		tab1.setWidget(0, 0, new MediumLabel("NIF arrendatario"));		
		tab1.setWidget(0, 1, new MediumLabel(AON.MSG.fullName()));
		
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
		tab1.setWidget(1, 1, name);
		
		panel.add(tab1);
		
		// Importe de la operación / Referencia Catastral / Provincia
		
		FlexTable tab2 = new FlexTable();
		tab2.getColumnFormatter().setWidth(0, "130px");
		tab2.getColumnFormatter().setWidth(1, "190px");
		tab2.getColumnFormatter().setWidth(2, "auto");
		
		tab2.setStyleName(AON.AON_CSS.aonWidthAll());
		tab2.addStyleName(AON.AON_CSS.aonNowrap());

		tab2.setWidget(0, 0, new MediumLabel(AON.MSG.amount()));
		tab2.setWidget(0, 1, new MediumLabel(AON.MSG.cadasdralReference()));		
		tab2.setWidget(0, 2, new MediumLabel(AON.MSG.province()));	

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

		tab3.setWidget(0, 0, new MediumLabel(AON.MSG.town()));
		tab3.setWidget(0, 1, new MediumLabel(AON.MSG.streetType()));
		tab3.setWidget(0, 2, new MediumLabel(AON.MSG.streetName()));		
		tab3.setWidget(0, 3, new MediumLabel(AON.MSG.streetNumber()));
		tab3.setWidget(0, 4, new MediumLabel(AON.MSG.streetStair()));
		tab3.setWidget(0, 5, new MediumLabel(AON.MSG.streetFloor()));
		tab3.setWidget(0, 6, new MediumLabel(AON.MSG.streetDoor()));
		
		TextBox town = new TextBox();
		town.setStyleName(AON.AON_CSS.aonInputText());
		town.setMaxLength(24);
		town.setVisibleLength(24);
		town.setValue(asset.getAssetStreetTown());
		town.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetTown(town.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab3.setWidget(1, 0, town);

		// Se graba según INE (hasta 5 caracteres), aunque luego el fichero lleva 2 caracteres (según AEAT)
		StreetTypeListBox streetType = new StreetTypeListBox();
		streetType.setValue(StreetType.getForIneCode(asset.getAssetStreetType()));
		streetType.addChangeHandler( new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				asset.setAssetStreetType(streetType.getValue(streetType.getSelectedIndex()));
				callback.onValueChanged(asset);
			}
		});
		tab3.setWidget(1, 1, streetType);
		
		TextBox streetName = new TextBox();
		streetName.setVisibleLength(25);
		streetName.setMaxLength(25);
		streetName.setStyleName(AON.AON_CSS.aonInputText());
		streetName.setValue(asset.getAssetStreet());
		streetName.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreet(streetName.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab3.setWidget(1, 2, streetName);
		
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
		
		TextBox stair = new TextBox();
		stair.setStyleName(AON.AON_CSS.aonInputText());
		stair.setMaxLength(2);
		stair.setVisibleLength(2);
		stair.setValue(asset.getAssetStreetStair());
		stair.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetStair(stair.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab3.setWidget(1, 4, stair);
		
		TextBox floor = new TextBox();
		floor.setStyleName(AON.AON_CSS.aonInputText());
		floor.setMaxLength(2);
		floor.setVisibleLength(2);
		floor.setValue(asset.getAssetStreetFloor());
		floor.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetFloor(floor.getValue());
				callback.onValueChanged(asset);				
			}
		});
		tab3.setWidget(1, 5, floor);
		
		TextBox door = new TextBox();
		door.setStyleName(AON.AON_CSS.aonInputText());
		door.setMaxLength(2);
		door.setVisibleLength(2);
		door.setValue(asset.getAssetStreetDoor());
		door.addValueChangeHandler(new ValueChangeHandler<String>() {
			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				asset.setAssetStreetDoor(door.getValue());
				callback.onValueChanged(asset);				
			}
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
