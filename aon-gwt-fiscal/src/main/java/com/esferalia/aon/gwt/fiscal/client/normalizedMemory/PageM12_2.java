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
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class PageM12_2 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageM12_2> {
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

	@UiField TabPanel tabPanel;
	
	public PageM12_2() {
		super();
		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		table4 = new FlexTable();
		table5 = new FlexTable();
		tabPanel = new TabPanel();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(0);
	}
	
	public PageM12_2(Enterprise enterprise, NormalizedMemory nm) {
		super();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		table4 = new FlexTable();
		table5 = new FlexTable();
		tabPanel = new TabPanel();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(0);
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

		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		table.getColumnFormatter().setWidth(2, "200px");
		table.getColumnFormatter().setWidth(3, "200px");
		table.getColumnFormatter().setWidth(4, "200px");
		table.getColumnFormatter().setWidth(5, "200px");
		table.getColumnFormatter().setWidth(6, "200px");
		table.getColumnFormatter().setWidth(7, "200px");

		int row = 0;
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
	
		table.setWidget(row, 1, new Label("Entidad dominante"));
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 2, new Label("Otras empresas del grupo"));
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 3, new Label("Negocios conjuntos en los que la empresa sea uno de los participantes"));
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 4, new Label("Empresas asociadas"));
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());

		table.setWidget(row, 5, new Label("Empresas con control conjunto o influencia significativa sobre la empresa"));
		table.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextCenter());

		table.setWidget(row, 6, new Label("Personal clave de la direcci\u00F3n de la empresa o de la entidad dominante"));
		table.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextCenter());

		table.setWidget(row, 7, new Label("Otras partes vinculadas"));
		table.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonTextCenter());

		
		++row;
		for(Integer i = 0; i< D2DepositConstants.MA12_ABREVIATE_KEYS_1.length; i+=7){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2DepositConstants.MA12_ABREVIATE_KEYS_1[i],
					D2DepositConstants.MA12_ABREVIATE_KEYS_1[i+1],
					D2DepositConstants.MA12_ABREVIATE_KEYS_1[i+2],
					D2DepositConstants.MA12_ABREVIATE_KEYS_1[i+3],
					D2DepositConstants.MA12_ABREVIATE_KEYS_1[i+4],
					D2DepositConstants.MA12_ABREVIATE_KEYS_1[i+5],
					D2DepositConstants.MA12_ABREVIATE_KEYS_1[i+6]
			};
			row = paintKey(table, d2 , row);
		}

	}
	
	private void table1(){
		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(1, "200px");
		table1.getColumnFormatter().setWidth(2, "200px");
		table1.getColumnFormatter().setWidth(3, "200px");
		table1.getColumnFormatter().setWidth(4, "200px");
		table1.getColumnFormatter().setWidth(5, "200px");
		table1.getColumnFormatter().setWidth(6, "200px");
		table1.getColumnFormatter().setWidth(7, "200px");

		int row = 0;
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
	
		table1.setWidget(row, 1, new Label("Entidad dominante"));
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 2, new Label("Otras empresas del grupo"));
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 3, new Label("Negocios conjuntos en los que la empresa sea uno de los participantes"));
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 4, new Label("Empresas asociadas"));
		table1.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());

		table1.setWidget(row, 5, new Label("Empresas con control conjunto o influencia significativa sobre la empresa"));
		table1.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextCenter());

		table1.setWidget(row, 6, new Label("Personal clave de la direcci\u00F3n de la empresa o de la entidad dominante"));
		table1.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextCenter());

		table1.setWidget(row, 7, new Label("Otras partes vinculadas"));
		table1.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonTextCenter());

		
		++row;
		for(Integer i = 0; i< D2DepositConstants.MA12_ABREVIATE_KEYS_2.length; i+=7){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2DepositConstants.MA12_ABREVIATE_KEYS_2[i],
					D2DepositConstants.MA12_ABREVIATE_KEYS_2[i+1],
					D2DepositConstants.MA12_ABREVIATE_KEYS_2[i+2],
					D2DepositConstants.MA12_ABREVIATE_KEYS_2[i+3],
					D2DepositConstants.MA12_ABREVIATE_KEYS_2[i+4],
					D2DepositConstants.MA12_ABREVIATE_KEYS_2[i+5],
					D2DepositConstants.MA12_ABREVIATE_KEYS_2[i+6]
			};
			row = paintKey(table1, d2 , row);
		}
	}
	private void table2(){
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
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
	
		table2.setWidget(row, 1, new Label("Entidad dominante"));
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table2.setWidget(row, 2, new Label("Otras empresas del grupo"));
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table2.setWidget(row, 3, new Label("Negocios conjuntos en los que la empresa sea uno de los participantes"));
		table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table2.setWidget(row, 4, new Label("Empresas asociadas"));
		table2.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());

		table2.setWidget(row, 5, new Label("Empresas con control conjunto o influencia significativa sobre la empresa"));
		table2.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextCenter());

		table2.setWidget(row, 6, new Label("Personal clave de la direcci\u00F3n de la empresa o de la entidad dominante"));
		table2.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextCenter());

		table2.setWidget(row, 7, new Label("Otras partes vinculadas"));
		table2.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonTextCenter());

		
		++row;
		
		//****************************** PYMES ****************************
		for(Integer i = 0; i< D2PDepositConstants.MP12_ABREVIATE_KEYS_3.length; i+=7){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2PDepositConstants.MP12_ABREVIATE_KEYS_3[i],
					D2PDepositConstants.MP12_ABREVIATE_KEYS_3[i+1],
					D2PDepositConstants.MP12_ABREVIATE_KEYS_3[i+2],
					D2PDepositConstants.MP12_ABREVIATE_KEYS_3[i+3],
					D2PDepositConstants.MP12_ABREVIATE_KEYS_3[i+4],
					D2PDepositConstants.MP12_ABREVIATE_KEYS_3[i+5],
					D2PDepositConstants.MP12_ABREVIATE_KEYS_3[i+6]
			};
			row = paintKey(table2, d2 , row);
		}

		//****************************** ABREVIADO ****************************		
//		for(Integer i = 0; i< D2DepositConstants.MA12_ABREVIATE_KEYS_3.length; i+=7){
//			D2DepositKey[] d2 = new D2DepositKey[]{
//					D2DepositConstants.MA12_ABREVIATE_KEYS_3[i],
//					D2DepositConstants.MA12_ABREVIATE_KEYS_3[i+1],
//					D2DepositConstants.MA12_ABREVIATE_KEYS_3[i+2],
//					D2DepositConstants.MA12_ABREVIATE_KEYS_3[i+3],
//					D2DepositConstants.MA12_ABREVIATE_KEYS_3[i+4],
//					D2DepositConstants.MA12_ABREVIATE_KEYS_3[i+5],
//					D2DepositConstants.MA12_ABREVIATE_KEYS_3[i+6]
//			};
//			row = paintKey(table2, d2 , row);
//		}
	}
	private void table3(){

		table3.setWidth("100%");
		table3.setCellSpacing(0);
		table3.getColumnFormatter().setWidth(1, "200px");
		table3.getColumnFormatter().setWidth(2, "200px");
		table3.getColumnFormatter().setWidth(3, "200px");
		table3.getColumnFormatter().setWidth(4, "200px");
		table3.getColumnFormatter().setWidth(5, "200px");
		table3.getColumnFormatter().setWidth(6, "200px");
		table3.getColumnFormatter().setWidth(7, "200px");

		int row = 0;
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
	
		table3.setWidget(row, 1, new Label("Entidad dominante"));
		table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 2, new Label("Otras empresas del grupo"));
		table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 3, new Label("Negocios conjuntos en los que la empresa sea uno de los participantes"));
		table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 4, new Label("Empresas asociadas"));
		table3.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());

		table3.setWidget(row, 5, new Label("Empresas con control conjunto o influencia significativa sobre la empresa"));
		table3.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextCenter());

		table3.setWidget(row, 6, new Label("Personal clave de la direcci\u00F3n de la empresa o de la entidad dominante"));
		table3.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextCenter());

		table3.setWidget(row, 7, new Label("Otras partes vinculadas"));
		table3.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonTextCenter());

		
		++row;
		for(Integer i = 0; i< D2DepositConstants.MA12_ABREVIATE_KEYS_4.length; i+=7){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2DepositConstants.MA12_ABREVIATE_KEYS_4[i],
					D2DepositConstants.MA12_ABREVIATE_KEYS_4[i+1],
					D2DepositConstants.MA12_ABREVIATE_KEYS_4[i+2],
					D2DepositConstants.MA12_ABREVIATE_KEYS_4[i+3],
					D2DepositConstants.MA12_ABREVIATE_KEYS_4[i+4],
					D2DepositConstants.MA12_ABREVIATE_KEYS_4[i+5],
					D2DepositConstants.MA12_ABREVIATE_KEYS_4[i+6]
			};
			row = paintKey(table3, d2 , row);
		}
	}
	private void table4(){
		table4.setWidth("100%");
		table4.setCellSpacing(0);
		table4.getColumnFormatter().setWidth(1, "200px");
		table4.getColumnFormatter().setWidth(2, "200px");


		int row = 0;
		table4.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table4.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table4.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());

		table4.setWidget(row, 1, new Label("Ejercicio 2014"));
		table4.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table4.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table4.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table4.setWidget(row, 2, new Label("Ejercicio 2013"));
		table4.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table4.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table4.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		
		++row;
		
		//**************************** PYMES *****************************
		for(Integer i = 0; i< D2PDepositConstants.MP12_ABREVIATE_KEYS_5.length; i+=2){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2PDepositConstants.MP12_ABREVIATE_KEYS_5[i],
					D2PDepositConstants.MP12_ABREVIATE_KEYS_5[i+1],
			};
			row = paintKey(table4, d2 , row);
		}
		
		//**************************** ABREVIADO *****************************
//		for(Integer i = 0; i< D2DepositConstants.MA12_ABREVIATE_KEYS_5.length; i+=2){
//			D2DepositKey[] d2 = new D2DepositKey[]{
//					D2DepositConstants.MA12_ABREVIATE_KEYS_5[i],
//					D2DepositConstants.MA12_ABREVIATE_KEYS_5[i+1],
//			};
//			row = paintKey(table4, d2 , row);
//		}
	}
	private void table5(){
		table5.setWidth("100%");
		table5.setCellSpacing(0);
		table5.getColumnFormatter().setWidth(1, "200px");
		table5.getColumnFormatter().setWidth(2, "200px");


		int row = 0;
		table5.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table5.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table5.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());

		table5.setWidget(row, 1, new Label("Ejercicio 2014"));
		table5.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table5.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table5.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table5.setWidget(row, 2, new Label("Ejercicio 2013"));
		table5.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table5.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table5.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		
		++row;
		
		//**************************** PYMES *****************************
		for(Integer i = 0; i< D2PDepositConstants.MP12_ABREVIATE_KEYS_6.length; i+=2){
			D2DepositKey[] d2 = new D2DepositKey[]{
					D2PDepositConstants.MP12_ABREVIATE_KEYS_6[i],
					D2PDepositConstants.MP12_ABREVIATE_KEYS_6[i+1],
			};
			row = paintKey(table5, d2 , row);
		}
		
		//**************************** ABREVIADO *****************************

//		for(Integer i = 0; i< D2DepositConstants.MA12_ABREVIATE_KEYS_6.length; i+=2){
//			D2DepositKey[] d2 = new D2DepositKey[]{
//					D2DepositConstants.MA12_ABREVIATE_KEYS_6[i],
//					D2DepositConstants.MA12_ABREVIATE_KEYS_6[i+1],
//			};
//			row = paintKey(table5, d2 , row);
//		}
	}
	
	
	protected int paintKey(FlexTable tab, D2DepositKey[] keys,  int row) {
		paintKeyDescription(tab, keys[0], row, 0);
		for (Integer i = 0; i < keys.length ; i++) {
			paintKeyField(tab,keys[i],row,i+1);
		}
		return  ++row;
	}

}
