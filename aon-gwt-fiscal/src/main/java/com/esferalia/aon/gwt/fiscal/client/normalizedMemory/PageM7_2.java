package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2PDepositConstants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class PageM7_2 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageM7_2> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table1;
	
	@UiField(provided = true)
	FlexTable table2;

	@UiField(provided = true)
	FlexTable table3;
	

	public PageM7_2() {
		super();
		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public PageM7_2(Enterprise enterprise, NormalizedMemory nm) {
		super();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}

	@Override
	protected void initializeTable() {
		table();
		table1();
		table2();
		//table3(); NO EN PYMES

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
		table.setWidget(row, 1, new Label("Deudas con entidades de cr\u00E9dito"));
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 2, new Label("Obligaciones y otros valores negociables"));
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 3, new Label("Derivados y otros"));
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
		
		//*************************** PYMES **********************
		for(Integer i = 0; i< D2PDepositConstants.MP7_ABREVIATE_KEYS_1.length; i+=8){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2PDepositConstants.MP7_ABREVIATE_KEYS_1[i],
					D2PDepositConstants.MP7_ABREVIATE_KEYS_1[i+1],
					D2PDepositConstants.MP7_ABREVIATE_KEYS_1[i+2],
					D2PDepositConstants.MP7_ABREVIATE_KEYS_1[i+3],
					D2PDepositConstants.MP7_ABREVIATE_KEYS_1[i+4],
					D2PDepositConstants.MP7_ABREVIATE_KEYS_1[i+5],
					D2PDepositConstants.MP7_ABREVIATE_KEYS_1[i+6],
					D2PDepositConstants.MP7_ABREVIATE_KEYS_1[i+7]
			};
			row = paintKey(table, d2 , row);
		}
		
		//*************************** ABREVIADO ************************
//		for(Integer i = 0; i< D2DepositConstants.MA7_ABREVIATE_KEYS_1.length; i+=8){
//			D2DepositKey[] d2 = new D2DepositKey[]{
//					D2DepositConstants.MA7_ABREVIATE_KEYS_1[i],
//					D2DepositConstants.MA7_ABREVIATE_KEYS_1[i+1],
//					D2DepositConstants.MA7_ABREVIATE_KEYS_1[i+2],
//					D2DepositConstants.MA7_ABREVIATE_KEYS_1[i+3],
//					D2DepositConstants.MA7_ABREVIATE_KEYS_1[i+4],
//					D2DepositConstants.MA7_ABREVIATE_KEYS_1[i+5],
//					D2DepositConstants.MA7_ABREVIATE_KEYS_1[i+6],
//					D2DepositConstants.MA7_ABREVIATE_KEYS_1[i+7]
//			};
//			row = paintKey(table, d2 , row);
//		}

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
		table1.setWidget(row, 1, new Label("Deudas con entidades de cr\u00E9dito"));
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 2, new Label("Obligaciones y otros valores negociables"));
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 3, new Label("Derivados y otros"));
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
		
		//*********************** PYMES *******************************
		for(Integer i = 0; i< D2PDepositConstants.MP7_ABREVIATE_KEYS_2.length; i+=8){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2PDepositConstants.MP7_ABREVIATE_KEYS_2[i],
					D2PDepositConstants.MP7_ABREVIATE_KEYS_2[i+1],
					D2PDepositConstants.MP7_ABREVIATE_KEYS_2[i+2],
					D2PDepositConstants.MP7_ABREVIATE_KEYS_2[i+3],
					D2PDepositConstants.MP7_ABREVIATE_KEYS_2[i+4],
					D2PDepositConstants.MP7_ABREVIATE_KEYS_2[i+5],
					D2PDepositConstants.MP7_ABREVIATE_KEYS_2[i+6],
					D2PDepositConstants.MP7_ABREVIATE_KEYS_2[i+7]
			};
			row = paintKey(table1, d2 , row);
		}

		//******************** ABREVIADO ***************************
//		for(Integer i = 0; i< D2DepositConstants.MA7_ABREVIATE_KEYS_2.length; i+=8){
//			D2DepositKey[] d2 = new D2DepositKey[]{
//					D2DepositConstants.MA7_ABREVIATE_KEYS_2[i],
//					D2DepositConstants.MA7_ABREVIATE_KEYS_2[i+1],
//					D2DepositConstants.MA7_ABREVIATE_KEYS_2[i+2],
//					D2DepositConstants.MA7_ABREVIATE_KEYS_2[i+3],
//					D2DepositConstants.MA7_ABREVIATE_KEYS_2[i+4],
//					D2DepositConstants.MA7_ABREVIATE_KEYS_2[i+5],
//					D2DepositConstants.MA7_ABREVIATE_KEYS_2[i+6],
//					D2DepositConstants.MA7_ABREVIATE_KEYS_2[i+7]
//			};
//			row = paintKey(table1, d2 , row);
//		}

	}
	
	private void table2(){
		FlexTable.FlexCellFormatter flexCellFormatter =table2.getFlexCellFormatter();

		flexCellFormatter.setColSpan(0, 1, 7);

		table2.setWidth("100%");
		table2.setCellSpacing(0);
		table2.getColumnFormatter().setWidth(1, "200px");
		table2.getColumnFormatter().setWidth(2, "200px");
		table2.getColumnFormatter().setWidth(3, "200px");
		table2.getColumnFormatter().setWidth(4, "200px");
		table2.getColumnFormatter().setWidth(5, "200px");
		table2.getColumnFormatter().setWidth(6, "200px");
		table2.getColumnFormatter().setWidth(7, "200px");


		int row = 0;
		table2.setWidget(row, 1, new Label("Vencimiento en años"));
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		
		++row;
		
		
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
	
			table2.setWidget(row, 1, new Label("Uno"));
			table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
			table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
			table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
			
			table2.setWidget(row, 2, new Label("Dos"));
			table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
			table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
			table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());

			table2.setWidget(row, 3, new Label("Tres"));
			table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
			table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
			table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());

			table2.setWidget(row, 4, new Label("Cuatro"));
			table2.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
			table2.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
			table2.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());

			table2.setWidget(row, 5, new Label("Cinco"));
			table2.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBold());
			table2.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBorderBottom());
			table2.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextCenter());

			table2.setWidget(row, 6, new Label("Mas de 5"));
			table2.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBold());
			table2.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBorderBottom());
			table2.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextCenter());

			table2.setWidget(row, 7, new Label("TOTAL"));
			table2.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBold());
			table2.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBorderBottom());
			table2.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonTextCenter());

		
		
		++row;
		for(Integer i = 0; i< D2DepositConstants.MA7_ABREVIATE_KEYS_3.length; i+=7){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2DepositConstants.MA7_ABREVIATE_KEYS_3[i],
					D2DepositConstants.MA7_ABREVIATE_KEYS_3[i+1],
					D2DepositConstants.MA7_ABREVIATE_KEYS_3[i+2],
					D2DepositConstants.MA7_ABREVIATE_KEYS_3[i+3],
					D2DepositConstants.MA7_ABREVIATE_KEYS_3[i+4],
					D2DepositConstants.MA7_ABREVIATE_KEYS_3[i+5],
					D2DepositConstants.MA7_ABREVIATE_KEYS_3[i+6]
			};
			row = paintKey(table2, d2 , row);
		}

	}
	
	private void table3(){
		table3.setWidth("100%");
		table3.setCellSpacing(0);
		table3.getColumnFormatter().setWidth(1, "200px");
		table3.getColumnFormatter().setWidth(2, "200px");
		table3.getColumnFormatter().setWidth(3, "200px");
	


		int row = 0;

		
		table3.setWidget(row, 1, new Label("Entidades de cr\u00E9dito"));
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
	
			table3.setWidget(row, 1, new Label("Limite concedido"));
			table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
			table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
			table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
			
			table3.setWidget(row, 2, new Label("Dispuesto"));
			table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
			table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
			table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());

			table3.setWidget(row, 3, new Label("Disponible"));
			table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
			table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
			table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());

		
		++row;
		for(Integer i = 0; i< D2DepositConstants.MA7_ABREVIATE_KEYS_4.length; i+=3){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2DepositConstants.MA7_ABREVIATE_KEYS_4[i],
					D2DepositConstants.MA7_ABREVIATE_KEYS_4[i+1],
					D2DepositConstants.MA7_ABREVIATE_KEYS_4[i+2]
			};
			row = paintKey(table3, d2 , row);
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
