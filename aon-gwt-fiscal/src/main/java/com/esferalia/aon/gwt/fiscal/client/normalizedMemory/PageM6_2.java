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

public class PageM6_2 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageM6_2> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table1;
	
	@UiField(provided = true)
	FlexTable table2;
	
	@UiField(provided = true)
	FlexTable table3;
	
	@UiField(provided = true)
	FlexTable table4;
	
	@UiField(provided = true)
	FlexTable table5;

	public PageM6_2() {
		super();
		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		table4 = new FlexTable();
		table5 = new FlexTable();
		
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public PageM6_2(Enterprise enterprise, NormalizedMemory nm) {
		super();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		table4 = new FlexTable();
		table5 = new FlexTable();
		
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}

	@Override
	protected void initializeTable() {
		table();
		table1();
		table2();
		table3();
		table4();
		table5();
	}
	
	private void table(){
		FlexTable.FlexCellFormatter flexCellFormatter =table.getFlexCellFormatter();
		flexCellFormatter.setColSpan(0, 1, 2);
		flexCellFormatter.setColSpan(0, 2, 2);
		flexCellFormatter.setColSpan(0, 3, 2);
		flexCellFormatter.setColSpan(0, 4, 2);
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		table.getColumnFormatter().setWidth(2, "200px");
		table.getColumnFormatter().setWidth(3, "200px");
		table.getColumnFormatter().setWidth(4, "200px");
		table.getColumnFormatter().setWidth(5, "200px");
		table.getColumnFormatter().setWidth(6, "200px");
		table.getColumnFormatter().setWidth(7, "200px");
		table.getColumnFormatter().setWidth(8, "200px");

		int row = 0;
	
		table.setWidget(row, 1, new Label("Instrumentos de patrimonio"));
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 2, new Label("Valores representables de deuda"));
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 3, new Label("Cr\u00e9ditos, derivados y otros"));
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 4, new Label("TOTAL"));
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());

		++row;
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		for(Integer i = 1; i<9;i+=2){
			table.setWidget(row, i, new Label("Ejercicio 2014"));
			table.getFlexCellFormatter().addStyleName(row, i, AON.AON_CSS.aonBold());
			table.getFlexCellFormatter().addStyleName(row, i, AON.AON_CSS.aonBorderBottom());
			table.getFlexCellFormatter().addStyleName(row, i, AON.AON_CSS.aonTextCenter());
			
			table.setWidget(row, i+1, new Label("Ejercicio 2013"));
			table.getFlexCellFormatter().addStyleName(row, i+1, AON.AON_CSS.aonBold());
			table.getFlexCellFormatter().addStyleName(row, i+1, AON.AON_CSS.aonBorderBottom());
			table.getFlexCellFormatter().addStyleName(row, i+1, AON.AON_CSS.aonTextCenter());

		}
		++row;
		for(Integer i = 0; i< D2DepositConstants.MA6_ABREVIATE_KEYS_1.length; i+=8){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2DepositConstants.MA6_ABREVIATE_KEYS_1[i],
					D2DepositConstants.MA6_ABREVIATE_KEYS_1[i+1],
					D2DepositConstants.MA6_ABREVIATE_KEYS_1[i+2],
					D2DepositConstants.MA6_ABREVIATE_KEYS_1[i+3],
					D2DepositConstants.MA6_ABREVIATE_KEYS_1[i+4],
					D2DepositConstants.MA6_ABREVIATE_KEYS_1[i+5],
					D2DepositConstants.MA6_ABREVIATE_KEYS_1[i+6],
					D2DepositConstants.MA6_ABREVIATE_KEYS_1[i+7]
			};
			row = paintKey(table, d2 , row);
		}

	}
	
	private void table1(){
		FlexTable.FlexCellFormatter flexCellFormatter =table1.getFlexCellFormatter();
		flexCellFormatter.setColSpan(0, 1, 2);
		flexCellFormatter.setColSpan(0, 2, 2);
		flexCellFormatter.setColSpan(0, 3, 2);
		flexCellFormatter.setColSpan(0, 4, 2);
		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(1, "200px");
		table1.getColumnFormatter().setWidth(2, "200px");
		table1.getColumnFormatter().setWidth(3, "200px");
		table1.getColumnFormatter().setWidth(4, "200px");
		table1.getColumnFormatter().setWidth(5, "200px");
		table1.getColumnFormatter().setWidth(6, "200px");
		table1.getColumnFormatter().setWidth(7, "200px");
		table1.getColumnFormatter().setWidth(8, "200px");

		int row = 0;
	
		table1.setWidget(row, 1, new Label("Instrumentos de patrimonio"));
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 2, new Label("Valores representables de deuda"));
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 3, new Label("Cr\u00e9ditos, derivados y otros"));
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 4, new Label("TOTAL"));
		table1.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());

		++row;
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		for(Integer i = 1; i<9;i+=2){
			table1.setWidget(row, i, new Label("Ejercicio 2014"));
			table1.getFlexCellFormatter().addStyleName(row, i, AON.AON_CSS.aonBold());
			table1.getFlexCellFormatter().addStyleName(row, i, AON.AON_CSS.aonBorderBottom());
			table1.getFlexCellFormatter().addStyleName(row, i, AON.AON_CSS.aonTextCenter());
			
			table1.setWidget(row, i+1, new Label("Ejercicio 2013"));
			table1.getFlexCellFormatter().addStyleName(row, i+1, AON.AON_CSS.aonBold());
			table1.getFlexCellFormatter().addStyleName(row, i+1, AON.AON_CSS.aonBorderBottom());
			table1.getFlexCellFormatter().addStyleName(row, i+1, AON.AON_CSS.aonTextCenter());

		}
		++row;
		for(Integer i = 0; i< D2DepositConstants.MA6_ABREVIATE_KEYS_2.length; i+=8){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2DepositConstants.MA6_ABREVIATE_KEYS_2[i],
					D2DepositConstants.MA6_ABREVIATE_KEYS_2[i+1],
					D2DepositConstants.MA6_ABREVIATE_KEYS_2[i+2],
					D2DepositConstants.MA6_ABREVIATE_KEYS_2[i+3],
					D2DepositConstants.MA6_ABREVIATE_KEYS_2[i+4],
					D2DepositConstants.MA6_ABREVIATE_KEYS_2[i+5],
					D2DepositConstants.MA6_ABREVIATE_KEYS_2[i+6],
					D2DepositConstants.MA6_ABREVIATE_KEYS_2[i+7]
			};
			row = paintKey(table1, d2 , row);
		}
	}
	private void table2(){
		FlexTable.FlexCellFormatter flexCellFormatter =table2.getFlexCellFormatter();
		flexCellFormatter.setColSpan(0, 1, 3);
		flexCellFormatter.setColSpan(0, 2, 3);
		table2.setWidth("100%");
		table2.setCellSpacing(0);
		table2.getColumnFormatter().setWidth(1, "200px");
		table2.getColumnFormatter().setWidth(2, "200px");
		table2.getColumnFormatter().setWidth(3, "200px");
		table2.getColumnFormatter().setWidth(4, "200px");
		table2.getColumnFormatter().setWidth(5, "200px");
		table2.getColumnFormatter().setWidth(6, "200px");

		int row = 0;
	
		table2.setWidget(row, 1, new Label("Ejercicio 2014"));
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table2.setWidget(row, 2, new Label("Ejercicio 2013"));
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		
		++row;
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		
		table2.setWidget(row, 1, new Label("Inversiones mantenidas hasta el vencimiento"));
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
			
		table2.setWidget(row, 2, new Label("Inversiones en el patrimonio de empresas del grupo, multigrupo y asociadas"));
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());

		table2.setWidget(row, 3, new Label("Activos financieros disponibles para la venta"));
		table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		
		table2.setWidget(row, 4, new Label("Inversiones mantenidas hasta el vencimiento"));
		table2.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());
		
		table2.setWidget(row, 5, new Label("Inversiones en el patrimonio de empresas del grupo, multigrupo y asociadas"));
		table2.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextCenter());
		
		table2.setWidget(row, 6, new Label("Activos financieros disponibles para la venta"));
		table2.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextCenter());
	
		++row;
		for(Integer i = 0; i< D2DepositConstants.MA6_ABREVIATE_KEYS_3.length; i+=6){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2DepositConstants.MA6_ABREVIATE_KEYS_3[i],
					D2DepositConstants.MA6_ABREVIATE_KEYS_3[i+1],
					D2DepositConstants.MA6_ABREVIATE_KEYS_3[i+2],
					D2DepositConstants.MA6_ABREVIATE_KEYS_3[i+3],
					D2DepositConstants.MA6_ABREVIATE_KEYS_3[i+4],
					D2DepositConstants.MA6_ABREVIATE_KEYS_3[i+5],
			};
			row = paintKey(table2, d2 , row);
		}
	}
	private void table3(){
		FlexTable.FlexCellFormatter flexCellFormatter =table3.getFlexCellFormatter();
		flexCellFormatter.setColSpan(0, 1, 2);
		flexCellFormatter.setColSpan(0, 2, 2);
		flexCellFormatter.setColSpan(0, 3, 2);

		table3.setWidth("100%");
		table3.setCellSpacing(0);
		table3.getColumnFormatter().setWidth(1, "200px");
		table3.getColumnFormatter().setWidth(2, "200px");
		table3.getColumnFormatter().setWidth(3, "200px");
		table3.getColumnFormatter().setWidth(4, "200px");
		table3.getColumnFormatter().setWidth(5, "200px");
		table3.getColumnFormatter().setWidth(6, "200px");


		int row = 0;
	
		table3.setWidget(row, 1, new Label("Valores representativos de deuda"));
		table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 2, new Label("Cr\u00e9ditos, derivados y otros"));
		table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 3, new Label("TOTAL"));
		table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
	
		++row;
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		for(Integer i = 1; i<7;i+=2){
			table3.setWidget(row, i, new Label("Largo plazo"));
			table3.getFlexCellFormatter().addStyleName(row, i, AON.AON_CSS.aonBold());
			table3.getFlexCellFormatter().addStyleName(row, i, AON.AON_CSS.aonBorderBottom());
			table3.getFlexCellFormatter().addStyleName(row, i, AON.AON_CSS.aonTextCenter());
			
			table3.setWidget(row, i+1, new Label("Corto plazo"));
			table3.getFlexCellFormatter().addStyleName(row, i+1, AON.AON_CSS.aonBold());
			table3.getFlexCellFormatter().addStyleName(row, i+1, AON.AON_CSS.aonBorderBottom());
			table3.getFlexCellFormatter().addStyleName(row, i+1, AON.AON_CSS.aonTextCenter());

		}
		++row;
		for(Integer i = 0; i< D2DepositConstants.MA6_ABREVIATE_KEYS_4.length; i+=6){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2DepositConstants.MA6_ABREVIATE_KEYS_4[i],
					D2DepositConstants.MA6_ABREVIATE_KEYS_4[i+1],
					D2DepositConstants.MA6_ABREVIATE_KEYS_4[i+2],
					D2DepositConstants.MA6_ABREVIATE_KEYS_4[i+3],
					D2DepositConstants.MA6_ABREVIATE_KEYS_4[i+4],
					D2DepositConstants.MA6_ABREVIATE_KEYS_4[i+5],
			};
			row = paintKey(table3, d2 , row);
		}
	}
	private void table4(){
		table4.setWidth("100%");
		table4.setCellSpacing(0);
		table4.getColumnFormatter().setWidth(1, "200px");
		table4.getColumnFormatter().setWidth(2, "200px");
		table4.getColumnFormatter().setWidth(3, "200px");
		table4.getColumnFormatter().setWidth(4, "200px");



		int row = 0;
		table4.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table4.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table4.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());

		table4.setWidget(row, 1, new Label("Activos a valor razonable con cambios en p\u00E9rdidas y ganancias"));
		table4.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table4.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table4.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table4.setWidget(row, 2, new Label("Activos mantenidos para negociar"));
		table4.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table4.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table4.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table4.setWidget(row, 3, new Label("Activos disponibles para la venta"));
		table4.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table4.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table4.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table4.setWidget(row, 4, new Label("TOTAL"));
		table4.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table4.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table4.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());
	
		
		++row;
		for(Integer i = 0; i< D2DepositConstants.MA6_ABREVIATE_KEYS_5.length; i+=4){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2DepositConstants.MA6_ABREVIATE_KEYS_5[i],
					D2DepositConstants.MA6_ABREVIATE_KEYS_5[i+1],
					D2DepositConstants.MA6_ABREVIATE_KEYS_5[i+2],
					D2DepositConstants.MA6_ABREVIATE_KEYS_5[i+3],
			};
			row = paintKey(table4, d2 , row);
		}
	}
	private void table5(){
		table5.setWidth("100%");
		table5.setCellSpacing(0);
		table5.getColumnFormatter().setWidth(1, "200px");
		table5.getColumnFormatter().setWidth(2, "200px");
		table5.getColumnFormatter().setWidth(3, "200px");
		table5.getColumnFormatter().setWidth(4, "200px");
		table5.getColumnFormatter().setWidth(5, "200px");
		table5.getColumnFormatter().setWidth(6, "200px");


		int row = 0;
		table5.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table5.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table5.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());

		table5.setWidget(row, 1, new Label("P\u00E9rdida por deterioro al final del ejercicio 2013"));
		table5.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table5.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table5.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table5.setWidget(row, 2, new Label("(+/-) Variaci\u00F3n deterioro a p\u00E9rdidas y ganancias"));
		table5.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table5.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table5.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table5.setWidget(row, 3, new Label("(+) Variaci\u00F3n contra patrimonio neto"));
		table5.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table5.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table5.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table5.setWidget(row, 4, new Label("(-) Salidas y reducciones"));
		table5.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table5.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table5.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());
		table5.setWidget(row, 5, new Label("(+/-) Traspasos y otras variaciones (combinaciones de negocio, etc.)"));
		table5.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBold());
		table5.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBorderBottom());
		table5.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextCenter());
		table5.setWidget(row, 6, new Label("P\u00E9rdida por deterioro al final del ejercicio 2014"));
		table5.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBold());
		table5.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBorderBottom());
		table5.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextCenter());
	
		
		++row;
		for(Integer i = 0; i< D2DepositConstants.MA6_ABREVIATE_KEYS_6.length; i+=6){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2DepositConstants.MA6_ABREVIATE_KEYS_6[i],
					D2DepositConstants.MA6_ABREVIATE_KEYS_6[i+1],
					D2DepositConstants.MA6_ABREVIATE_KEYS_6[i+2],
					D2DepositConstants.MA6_ABREVIATE_KEYS_6[i+3],
					D2DepositConstants.MA6_ABREVIATE_KEYS_6[i+4],
					D2DepositConstants.MA6_ABREVIATE_KEYS_6[i+5],
			};
			row = paintKey(table5, d2 , row);
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
