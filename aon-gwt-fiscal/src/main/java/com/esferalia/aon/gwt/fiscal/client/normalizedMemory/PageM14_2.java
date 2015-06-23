package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PageM14_2 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageM14_2> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table1;
	
	@UiField(provided = true)
	FlexTable table2;
	
	public PageM14_2() {
		super();
		table1 = new FlexTable();
		table2 = new FlexTable();

		
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public PageM14_2(Enterprise enterprise, NormalizedMemory nm) {
		super();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		table1 = new FlexTable();
		table2 = new FlexTable();
	
		
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}

	@Override
	protected void initializeTable() {
		table();
		table1();
		table2();

	}
	
	private void table(){
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		table.getColumnFormatter().setWidth(2, "200px");


		int row = 0;
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());

		table.setWidget(row, 1, new Label("Ejercicio 2014"));
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 2, new Label("Ejercicio 2013"));
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		
		++row;
		table.setWidget(row, 0, new Label("A) ACTIVOS DE NATURALEZA MEDIOAMBIENTAL"));
		++row;
		for(Integer i = 0; i< D2DepositConstants.MA14_ABREVIATE_KEYS_1.length; i+=2){
			if(row == 4){
				table.setWidget(row, 0, new Label("3.Correcciones valorativas por deterioro"));
				++row;
			}
			if(row == 8){
				table.setWidget(row, 0, new Label("C)Riesgos cubiertos por las provisiones para actuaciones medioambientales"));
				++row;
			}
			if(row == 9){
				table.setWidget(row, 0, new Label("1.Provisi\u00F3n para actuaciones medioambientales, inclu\u00EDdas en provisiones"));
				++row;
			}
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2DepositConstants.MA14_ABREVIATE_KEYS_1[i],
					D2DepositConstants.MA14_ABREVIATE_KEYS_1[i+1],
			};
			row = paintKey(table, d2 , row);
		}

	}
	
	private void table1(){
		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(1, "200px");
	


		int row = 0;
		table1.setWidget(row, 0, new Label("Derechos de emisi\u00F3n de gases de efecto invernadero"));
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());

		table1.setWidget(row, 1, new Label("Importe"));
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		
		++row;
		for(Integer i = 0; i< D2DepositConstants.MA14_ABREVIATE_KEYS_2.length; i++){

			row = paintKey(table1, D2DepositConstants.MA14_ABREVIATE_KEYS_2[i] , row);
		}
	}
	private void table2(){
		table2.setWidth("100%");
		table2.setCellSpacing(0);
		table2.getColumnFormatter().setWidth(1, "200px");
	


		int row = 0;
		table2.setWidget(row, 0, new Label("Concepto"));
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());

		table2.setWidget(row, 1, new Label("Importe"));
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		
		++row;
		for(Integer i = 0; i< D2DepositConstants.MA14_ABREVIATE_KEYS_3.length; i++){

			row = paintKey(table2, D2DepositConstants.MA14_ABREVIATE_KEYS_3[i] , row);
		}
	}
	
	
	protected int paintKey(FlexTable tab, D2DepositKey[] keys,  int row) {
		paintKeyDescription(tab, keys[0], row, 0);
		for (Integer i = 0; i < keys.length ; i++) {
			paintKeyField(tab,keys[i],row,i+1);
		}
		return  ++row;
	}

}
