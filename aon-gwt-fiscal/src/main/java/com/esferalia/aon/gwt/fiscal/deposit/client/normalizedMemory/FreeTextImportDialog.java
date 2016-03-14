package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public abstract class FreeTextImportDialog extends CustomDialogB {
	interface Binder extends UiBinder<Widget, FreeTextImportDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField(provided = true) FlexTable flex_table;
	@UiField(provided = true) Label label;
	@UiField Button accept_button;
	@UiField Button cancel_button;
	
	public FreeTextImportDialog() {
		setCaption("Importar Texto");
		label =  new Label("Seleccionar apartados:");
		label.setStyleName(AON.AON_BOLD);
		flex_table = new FlexTable();
		setWidget(binder.createAndBindUi(this));
		buildTable();
		accept_button.setText("Aceptar");
		accept_button.addClickHandler(new ClickHandler() {
			@Override public void onClick(ClickEvent event) {
				onAccept();
			}
		});
		
		cancel_button.setText("Cancelar");
		cancel_button.setVisible(false);
		cancel_button.addClickHandler(new ClickHandler() {			
			@Override public void onClick(ClickEvent event) {
				onCancel();
			}
		});
	}
	
	protected abstract void onAccept();
	
	protected abstract void onCancel();
	
	private static final String AP1 = "Apartado 1: Actividad de la empresa";
	private static final String AP2 = "Apartado 2: Bases de presentaci\u00f3n de las cuentas anuales";
	private static final String AP3 = "Apartado 3: Aplicaci\u00f3n de resultados";
	private static final String AP4 = "Apartado 4: Normas de registro y valoraci\u00f3n";
	private static final String AP5 = "Apartado 5: Inmovilizado material, intangible, e inversiones inmobiliarias";
	private static final String AP6 = "Apartado 6: Activos financieros";
	private static final String AP7 = "Apartado 7: Pasivos financieros";
	private static final String AP8 = "Apartado 8: Fondos propios";
	private static final String AP9 = "Apartado 9: Situaci\u00f3n fiscal";
	private static final String AP11 = "Apartado 11: Subvenciones, donaciones y legados";
	private static final String AP12 = "Apartado 12: Operaciones con partes vinculadas";
	private static final String AP13 = "Apartado 13: Otra informaci\u00f3n";
	private static final String AP14 = "Apartado 14: Informaci\u00f3n sobre medio ambiente";

	private void buildTable() {
		flex_table.setWidget(0, 0, new Label(AP1));
		CheckBox cb0 = new CheckBox();cb0.setValue(true);
		flex_table.setWidget(0, 1, cb0);
		
		flex_table.setWidget(1, 0, new Label(AP2));
		CheckBox cb1 = new CheckBox();cb1.setValue(true);
		flex_table.setWidget(1, 1, cb1);
	
		flex_table.setWidget(2, 0, new Label(AP3));
		CheckBox cb2 = new CheckBox();cb2.setValue(true);
		flex_table.setWidget(2, 1, cb2);
	
		flex_table.setWidget(3, 0, new Label(AP4));
		CheckBox cb3 = new CheckBox();cb3.setValue(true);
		flex_table.setWidget(3, 1, cb3);
		
		flex_table.setWidget(4, 0, new Label(AP5));
		CheckBox cb4 = new CheckBox();cb4.setValue(true);
		flex_table.setWidget(4, 1, cb4);
	
		flex_table.setWidget(5, 0, new Label(AP6));
		CheckBox cb5 = new CheckBox();cb5.setValue(true);
		flex_table.setWidget(5, 1, cb5);

		flex_table.setWidget(6, 0, new Label(AP7));
		CheckBox cb6 = new CheckBox();cb6.setValue(true);
		flex_table.setWidget(6, 1, cb6);
		
		flex_table.setWidget(7, 0, new Label(AP8));
		CheckBox cb7 = new CheckBox();cb7.setValue(true);
		flex_table.setWidget(7, 1, cb7);
		
		flex_table.setWidget(8, 0, new Label(AP9));
		CheckBox cb8 = new CheckBox();cb8.setValue(true);
		flex_table.setWidget(8, 1, cb8);
		
		flex_table.setWidget(9, 0, new Label(AP11));
		CheckBox cb9 = new CheckBox();cb9.setValue(true);
		flex_table.setWidget(9, 1, cb9);
		
		flex_table.setWidget(10, 0, new Label(AP12));
		CheckBox cb10 = new CheckBox();cb10.setValue(true);
		flex_table.setWidget(10, 1, cb10);
		
		flex_table.setWidget(11, 0, new Label(AP13));
		CheckBox cb11 = new CheckBox();cb11.setValue(true);
		flex_table.setWidget(11, 1, cb11);
		
		flex_table.setWidget(12, 0, new Label(AP14));
		CheckBox cb12 = new CheckBox();cb12.setValue(true);
		flex_table.setWidget(12, 1, cb12);
	}
	
	public D2DepositKey getD2DepositKey(Integer k){
		if(k.equals(0)) return D2DepositKey.MAT19019001;
		if(k.equals(1)) return D2DepositKey.MAT29029001;
		if(k.equals(2)) return D2DepositKey.MAT39039001;
		if(k.equals(3)) return D2DepositKey.MAT49049001;
		if(k.equals(4)) return D2DepositKey.MAT59059001;
		if(k.equals(5)) return D2DepositKey.MAT69069001;
		if(k.equals(6)) return D2DepositKey.MAT79079001;
		if(k.equals(7)) return D2DepositKey.MAT89089001;
		if(k.equals(8)) return D2DepositKey.MAT99099001;
		if(k.equals(9)) return D2DepositKey.MAT119119001;
		if(k.equals(10)) return D2DepositKey.MAT129129001;
		if(k.equals(11)) return D2DepositKey.MAT139139001;
		if(k.equals(12)) return D2DepositKey.MAT149149001;
		return null;
	}
	
}
