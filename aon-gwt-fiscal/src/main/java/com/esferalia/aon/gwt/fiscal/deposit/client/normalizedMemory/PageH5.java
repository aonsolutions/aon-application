package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class PageH5 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageH5> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField
	Label LMA8099000;
	@UiField
	CheckBox IMA8099000;
	@UiField
	Label LMA8099010;
	@UiField
	CheckBox IMA8099010;
	@UiField
	TabPanel tabPanel;

	public PageH5() {
		super();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		init();
		tabPanel.selectTab(0);
	}

	public PageH5(Enterprise enterprise, NormalizedMemory nm, Integer year) {
		super();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		this.year = year;
		
		LMA8099000 = new Label(); 
		IMA8099000 = new CheckBox();
		LMA8099010 = new Label();
		IMA8099010 = new CheckBox();
		
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(0);
	}

	private void init() {
		LMA8099000.setText("Los abajo firmantes, como Administradores de "
				+ "la Sociedad citada, manifiestan que en la contabilidad "
				+ "correspondiente a las presentes cuentas anuales NO existe "
				+ "ninguna partida de naturaleza medioambiental que deba ser "
				+ "inclu\u00EDda de acuerdo a la norma de elaboraci\u00F3n "
				+ "'4\u00BA Cuentas anuales abreviadas' en su punto 5, de "
				+ "la tercera parte del Plan General de Contabilidad (Real "
				+ "Decreto 1514/2007 de 16 de Noviembre).");
		LMA8099010.setText("Los abajo firmantes, como Administradores de la "
				+ "Sociedad citada, manifiestan que en la contabilidad "
				+ "correspondiente a las presentes cuentas anuales SI existen "
				+ "partidas de naturaleza medioambiental, y han sido inclu\u00EDdas "
				+ "en un Apartado adiciones de la Memoria de acuerdo a la norma "
				+ "de elaboraci\u00F3n '4\u00BA Cuentas anuales abreviadas' en "
				+ "su punto 5, de la tercera parte del Plan General de Contabilidad (Real "
				+ "Decreto 1514/2007 de 16 de Noviembre).");
		if (map.containsKey(D2DepositHeaderKey.IMA8099000.getCode())) {
			String value = map.get(D2DepositHeaderKey.IMA8099000.getCode());
			IMA8099000.setValue(value.equals("1"));
		}
		if (map.containsKey(D2DepositHeaderKey.IMA8099010.getCode())) {
			String value = map.get(D2DepositHeaderKey.IMA8099010.getCode());
			IMA8099010.setValue(value.equals("1"));
		}
		
		IMA8099000.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				normalizedMemory.saveButton.setEnabled(true);
				normalizedMemory.cancelButton.setVisible(true);
				onEdit(D2DepositHeaderKey.IMA8099000.getCode(),
						event.getValue() ? "1" : "0");
				if (event.getValue()) {
					IMA8099010.setValue(false);
					onEdit(D2DepositHeaderKey.IMA8099010.getCode(), "0");
				}
			}
		});
		IMA8099010.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				normalizedMemory.saveButton.setEnabled(true);
				normalizedMemory.cancelButton.setVisible(true);
				onEdit(D2DepositHeaderKey.IMA8099010.getCode(),
						event.getValue() ? "1" : "0");
				if (event.getValue()) {
					IMA8099000.setValue(false);
					onEdit(D2DepositHeaderKey.IMA8099000.getCode(), "0");
				}
			}
		});
	}

	@Override
	protected void initializeTable() {
		init();
	}

	@Override
	protected void onEdit(String key, String value) {
			if(mapDraft.containsKey(key))
				mapDraft.remove(key);
			mapDraft.put(key, value);
			normalizedMemory.getD2Deposit().setMapDraft(mapDraft);
			normalizedMemory.getD2Deposit().setModify(true);		
	}
}
