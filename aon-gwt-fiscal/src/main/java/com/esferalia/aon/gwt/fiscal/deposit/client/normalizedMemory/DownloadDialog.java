package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.CustomDialogB;
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

public abstract class DownloadDialog extends CustomDialogB {
	interface Binder extends UiBinder<Widget, DownloadDialog>{
		
	}
	private static final Binder binder = GWT.create(Binder.class);
	
	@UiField(provided = true) FlexTable flex_table;
	@UiField Button accept_button;
	@UiField Button cancel_button;
	
	public DownloadDialog(Boolean isMemory, Boolean isMa) {
		setCaption("Descargar");
		flex_table = new FlexTable();
		setWidget(binder.createAndBindUi(this));
		buildTable(isMemory, isMa);
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
	private static final String IDA = "Hoja Identificativa de la sociedad";
	private static final String BS = "Balance de situaci\u00f3n";
	private static final String PYG = "Cuenta de perdidas y ganancias";
	private static final String ECPN = "Estado de cambios en el patrimonio neto";
	private static final String DM = "Declaraci\u00f3n medioambiental";

	private static final String AP1 = "Apartado 1: Actividad de la empresa";
	private static final String AP2 = "Apartado 2: Bases de presentaci\u00f3n de las cuentas anuales";
	private static final String AP3 = "Apartado 3: Aplicaci\u00f3n de resultados";
	private static final String AP4 = "Apartado 4: Normas de registro y valoraci\u00f3n";
	private static final String AP5 = "Apartado 5: Inmovilizado material, intangible, e inversiones inmobiliarias";
	private static final String AP6 = "Apartado 6: Activos financieros";
	private static final String AP7 = "Apartado 7: Pasivos financieros";
	private static final String AP8 = "Apartado 8: Fondos propios";
	private static final String AP9 = "Apartado 9: Situaci\u00f3n fiscal";
	private static final String AP10 = "Apartado 10: Ingresos y gastos";
	private static final String AP11 = "Apartado 11: Subvenciones, donaciones y legados";
	private static final String AP12 = "Apartado 12: Operaciones con partes vinculadas";
	private static final String AP13 = "Apartado 13: Otra informaci\u00f3n";
	private static final String AP14 = "Apartado 14: Informaci\u00f3n sobre medio ambiente";
	private static final String AP15 = "Apartado 15: Informaci\u00f3n sobre los aplazamientos de pago efectuados a proveedores";
	private static final String MA = "Modelo de autocartera";
	private static final String IP = "Instancia de presentaci\u00f3n";
	private static final String CHD = "Certificaci\u00f3n de la huella digital";

	CheckBox cb,cb0,cb1,cb2,cb3,cb4,cb5,cb6,cb7,cb8,cb9,cb10,cb11,cb12,cb13,cb14,cb15,cb16,cb17,cb18,cb19,cb20,cb21,cb22;
	private void buildTable(final Boolean isMemory, final Boolean isMa) {
		
		flex_table.setWidget(1, 0, new Label(IDA));
		cb0 = new CheckBox();cb0.setValue(true);
		flex_table.setWidget(1, 1, cb0);
		
		flex_table.setWidget(2, 0, new Label(BS));
		cb1 = new CheckBox();cb1.setValue(true);
		flex_table.setWidget(2, 1, cb1);
	
		flex_table.setWidget(3, 0, new Label(PYG));
		cb2 = new CheckBox();cb2.setValue(true);
		flex_table.setWidget(3, 1, cb2);
	
		flex_table.setWidget(4, 0, new Label(ECPN));
		cb3 = new CheckBox();cb3.setValue(true);
		flex_table.setWidget(4, 1, cb3);
		
		flex_table.setWidget(5, 0, new Label(DM));
		cb4 = new CheckBox();cb4.setValue(true);
		flex_table.setWidget(5, 1, cb4);
	

		flex_table.setWidget(6, 0, new Label(AP1));
		cb5 = new CheckBox();cb5.setValue(!isMemory);
		cb5.setEnabled(!isMemory);
		flex_table.setWidget(6, 1, cb5);

		flex_table.setWidget(7, 0, new Label(AP2));
		cb6 = new CheckBox();cb6.setValue(!isMemory);
		cb6.setEnabled(!isMemory);
		flex_table.setWidget(7, 1, cb6);
			
		flex_table.setWidget(8, 0, new Label(AP3));
		cb7 = new CheckBox();cb7.setValue(!isMemory);
		cb7.setEnabled(!isMemory);
		flex_table.setWidget(8, 1, cb7);
			
		flex_table.setWidget(9, 0, new Label(AP4));
		cb8 = new CheckBox();cb8.setValue(!isMemory);
		cb8.setEnabled(!isMemory);
		flex_table.setWidget(9, 1, cb8);
			
		flex_table.setWidget(10, 0, new Label(AP5));
		cb9 = new CheckBox();cb9.setValue(!isMemory);
		cb9.setEnabled(!isMemory);
		flex_table.setWidget(10, 1, cb9);
			
		flex_table.setWidget(11, 0, new Label(AP6));
		cb10 = new CheckBox();cb10.setValue(!isMemory);
		cb10.setEnabled(!isMemory);
		flex_table.setWidget(11, 1, cb10);
		
		flex_table.setWidget(12, 0, new Label(AP7));
		cb11 = new CheckBox();cb11.setValue(!isMemory);
		cb11.setEnabled(!isMemory);
		flex_table.setWidget(12, 1, cb11);
		
		flex_table.setWidget(13, 0, new Label(AP8));
		cb12 = new CheckBox();cb12.setValue(!isMemory);
		cb12.setEnabled(!isMemory);
		flex_table.setWidget(13, 1, cb12);
		
		flex_table.setWidget(14, 0, new Label(AP9));
		cb13 = new CheckBox();cb13.setValue(!isMemory);
		cb13.setEnabled(!isMemory);
		flex_table.setWidget(14, 1, cb13);
		
		flex_table.setWidget(15, 0, new Label(AP10));
		cb14 = new CheckBox();cb14.setValue(!isMemory);
		cb14.setEnabled(!isMemory);
		flex_table.setWidget(15, 1, cb14);
		
		flex_table.setWidget(16, 0, new Label(AP11));
		cb15 = new CheckBox();cb15.setValue(!isMemory);
		cb15.setEnabled(!isMemory);
		flex_table.setWidget(16, 1, cb15);
			
		flex_table.setWidget(17, 0, new Label(AP12));
		cb16 = new CheckBox();cb16.setValue(!isMemory);
		cb16.setEnabled(!isMemory);
		flex_table.setWidget(17, 1, cb16);
		
		flex_table.setWidget(18, 0, new Label(AP13));
		cb17 = new CheckBox();cb17.setValue(!isMemory);
		cb17.setEnabled(!isMemory);
		flex_table.setWidget(18, 1, cb17);
	
		flex_table.setWidget(19, 0, new Label(AP14));
		cb18 = new CheckBox();cb18.setValue(!isMemory);
		cb18.setEnabled(!isMemory);
		flex_table.setWidget(19, 1, cb18);
		
		flex_table.setWidget(20, 0, new Label(AP15));
		cb19 = new CheckBox();cb19.setValue(!isMemory);
		cb19.setEnabled(!isMemory);
		flex_table.setWidget(20, 1, cb19);
	
		flex_table.setWidget(21, 0, new Label(MA));
		cb20 = new CheckBox();cb20.setValue(!isMa);
		cb20.setEnabled(!isMa);
		flex_table.setWidget(21, 1, cb20);
		
		flex_table.setWidget(22, 0, new Label(IP));
		cb21 = new CheckBox();cb21.setValue(true);
		flex_table.setWidget(22, 1, cb21);
		
		flex_table.setWidget(23, 0, new Label(CHD));
		cb22 = new CheckBox();cb22.setValue(true);
		flex_table.setWidget(23, 1, cb22);
		
		Label label =  new Label("Seleccionar apartados:");
		label.setStyleName(AON.AON_BOLD);
		flex_table.setWidget(0, 0, label);
		cb = new CheckBox();cb.setValue(true);
		cb.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Boolean bool = cb.getValue();
				cb0.setValue(bool);
				cb1.setValue(bool);
				cb2.setValue(bool);
				cb3.setValue(bool);
				cb4.setValue(bool);
				
				cb5.setValue(!isMemory ? bool : false);
				cb6.setValue(!isMemory ? bool : false);
				cb7.setValue(!isMemory ? bool : false);
				cb8.setValue(!isMemory ? bool : false);
				cb9.setValue(!isMemory ? bool : false);
				cb10.setValue(!isMemory ? bool : false);
				cb11.setValue(!isMemory ? bool : false);
				cb12.setValue(!isMemory ? bool : false);
				cb13.setValue(!isMemory ? bool : false);
				cb14.setValue(!isMemory ? bool : false);
				cb15.setValue(!isMemory ? bool : false);
				cb16.setValue(!isMemory ? bool : false);
				cb17.setValue(!isMemory ? bool : false);
				cb18.setValue(!isMemory ? bool : false);
				cb19.setValue(!isMemory ? bool : false);
				
				cb20.setValue(!isMa ? bool : false);
				
				cb21.setValue(bool);
				cb22.setValue(bool);
			}
		});
		flex_table.setWidget(0, 1, cb);
		
	}
	
}
