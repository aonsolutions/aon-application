package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.client.widget.DoubleBox;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositFooterKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

public class PageF1 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageF1> {
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
	
	@UiField(provided = true)
	FlexTable table6;
	
	@UiField(provided = true)
	FlexTable table7;

	@UiField CheckBox A18009050;
	
	@UiField DoubleBox A18009010;
	@UiField DoubleBox A18009020;
	@UiField DoubleBox A18009030;
	@UiField DoubleBox A18009040;
	
	
	@UiField TabPanel tabPanel;
	
	public PageF1() {
		super();
		
		A18009010 = new DoubleBox();
		A18009020 = new DoubleBox();
		A18009030 = new DoubleBox();
		A18009040 = new DoubleBox();
		A18009050 = new CheckBox();
		
		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		table4 = new FlexTable();
		table5 = new FlexTable();
		table6 = new FlexTable();
		table7 = new FlexTable();
		
		tabPanel = new TabPanel();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(0);
	}
	
	public PageF1(Enterprise enterprise, NormalizedMemory nm) {
		super();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		
		A18009010 = new DoubleBox();
		A18009020 = new DoubleBox();
		A18009030 = new DoubleBox();
		A18009040 = new DoubleBox();
		A18009050 = new CheckBox();
		
		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		table4 = new FlexTable();
		table5 = new FlexTable();
		table6 = new FlexTable();
		table7 = new FlexTable();
		tabPanel = new TabPanel();
		
		
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);

		tabPanel.selectTab(0);
	}

	private void init(){

				keyExe(map, "8009010", A18009010, "double", true);
				keyExe(map, "8009020", A18009020, "double", true);
				keyExe(map, "8009030", A18009030, "double", true);
				keyExe(map, "8009040", A18009040, "double", true);
				keyExe(map, "8009050", A18009050, "check", true);
				
				
	
	}
	
	
	@Override
	protected void initializeTable() {
		init();
		table();
		table1();
		table2();
		table3();
		table4();
		table5();
		table6();
		table7();
	}
	
	private void table(){

		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(0, "200px");
		table.getColumnFormatter().setWidth(1, "200px");
		table.getColumnFormatter().setWidth(2, "200px");
		table.getColumnFormatter().setWidth(3, "200px");
		table.getColumnFormatter().setWidth(4, "200px");
		table.getColumnFormatter().setWidth(5, "200px");
		table.getColumnFormatter().setWidth(6, "200px");
		table.getColumnFormatter().setWidth(7, "200px");
	

		int row = 0;
		table.setWidget(row, 0, new Label("Fecha"));
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 1, new Label("Concepto"));
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 2, new Label("Fecha de acuerdo de junta general"));
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 3, new Label("N\u00AA de acciones / participaciones"));
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 4, new Label("Nominal"));
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());
		
		table.setWidget(row, 5, new Label("Capital social porcentaje"));
		table.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 6, new Label("Precio o contraprestaci\u00F3n"));
		table.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextCenter());
		table.setWidget(row, 7, new Label("Saldo despu\u00E9s de la operaci\u00F3n"));
		table.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonTextCenter());
	
		++row;

		for(Integer i = 0; i< D2DepositConstants.A1_ABREVIATE_KEYS_1.length; i+=8){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+1],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+2],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+3],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+4],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+5],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+6],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+7],

			};
			for (Integer j = 0; j < d2.length ;j++) {
				if(j == 0 || j == 2)
					paintDateKeyField(table, d2[j], row, j);
				else if(j == 1)
					paintListKeyField(table, d2[j], row, j);
				else
					paintDoubleKeyField(table ,d2[j],row,j);
			}
			++row;		
		}

	}
	
	
	private void table1(){


		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(0, "200px");
		table1.getColumnFormatter().setWidth(1, "200px");
		table1.getColumnFormatter().setWidth(2, "200px");
		table1.getColumnFormatter().setWidth(3, "200px");
		table1.getColumnFormatter().setWidth(4, "200px");
		table1.getColumnFormatter().setWidth(5, "200px");
		table1.getColumnFormatter().setWidth(6, "200px");
		table1.getColumnFormatter().setWidth(7, "200px");
	

		int row = 0;
		table1.setWidget(row, 0, new Label("Fecha"));
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 1, new Label("Concepto"));
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 2, new Label("Fecha de acuerdo de junta general"));
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 3, new Label("N\u00AA de acciones / participaciones"));
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 4, new Label("Nominal"));
		table1.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());
	
		table1.setWidget(row, 5, new Label("Capital social porcentaje"));
		table1.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 5, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 6, new Label("Precio o contraprestaci\u00F3n"));
		table1.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 6, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 7, new Label("Saldo despu\u00E9s de la operaci\u00F3n"));
		table1.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 7, AON.AON_CSS.aonTextCenter());

		++row;

		for(Integer i = 0; i< D2DepositConstants.A11_ABREVIATE_KEYS.length; i+=8){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A11_ABREVIATE_KEYS[i],
					D2DepositConstants.A11_ABREVIATE_KEYS[i+1],
					D2DepositConstants.A11_ABREVIATE_KEYS[i+2],
					D2DepositConstants.A11_ABREVIATE_KEYS[i+3],
					D2DepositConstants.A11_ABREVIATE_KEYS[i+4],
					D2DepositConstants.A11_ABREVIATE_KEYS[i+5],
					D2DepositConstants.A11_ABREVIATE_KEYS[i+6],
					D2DepositConstants.A11_ABREVIATE_KEYS[i+7],

			};
			for (Integer j = 0; j < d2.length ;j++) {
				if(j == 0 || j == 2)
					paintDateKeyField(table1, d2[j], row, j);
				else if(j == 1)
					paintListKeyField(table1, d2[j], row, j);
				else
					paintDoubleKeyField(table1 ,d2[j],row,j);
			}
			++row;		
		}

	}
	
	
	private void table2(){

		table2.setWidth("100%");
		table2.setCellSpacing(0);
		table2.getColumnFormatter().setWidth(0, "200px");
		table2.getColumnFormatter().setWidth(1, "200px");
	
	

		int row = 0;
		table2.setWidget(row, 0, new Label("Fecha acuerdo"));
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table2.setWidget(row, 1, new Label("Transcripci\u00F3n literal del acuerdo"));
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		

		++row;
		for(Integer i = 0; i< D2DepositConstants.A2_ABREVIATE_KEYS.length; i+=2){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A2_ABREVIATE_KEYS[i],
					D2DepositConstants.A2_ABREVIATE_KEYS[i+1],
					

			};
			for (Integer j = 0; j < d2.length ;j++) {
				if(j == 1)
					paintTextKeyField(table2, d2[j], row, j);
				else if(j == 0  )
					paintDateKeyField(table2, d2[j], row, j);
			}
			++row;		
		}

	}
	
	private void table3(){

		table3.setWidth("100%");
		table3.setCellSpacing(0);
		table3.getColumnFormatter().setWidth(0, "200px");
		table3.getColumnFormatter().setWidth(1, "200px");
		table3.getColumnFormatter().setWidth(2, "200px");
		table3.getColumnFormatter().setWidth(3, "200px");

	

		int row = 0;
		table3.setWidget(row, 0, new Label("Fecha"));
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 1, new Label("Relaci\u00F3n numerada de las acciones / participaciones"));
		table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 2, new Label("T\u00EDtulo de adquisici\u00F3n"));
		table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 3, new Label("% sobre capital"));
		table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
	
		++row;
		for(Integer i = 0; i< D2DepositConstants.A3_ABREVIATE_KEYS.length; i+=4){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A3_ABREVIATE_KEYS[i],
					D2DepositConstants.A3_ABREVIATE_KEYS[i+1],
					D2DepositConstants.A3_ABREVIATE_KEYS[i+2],
					D2DepositConstants.A3_ABREVIATE_KEYS[i+3],


			};
			for (Integer j = 0; j < d2.length ;j++) {
				if(j == 1 || j == 2)
					paintTextKeyField(table3, d2[j], row, j);
				else if(j == 0  )
					paintDateKeyField(table3, d2[j], row, j);
				else if(j == 3)
					paintDoubleKeyField(table3 ,d2[j],row,j);
			}
			++row;		
		}

	}
	
	private void table4(){

		table4.setWidth("100%");
		table4.setCellSpacing(0);
		table4.getColumnFormatter().setWidth(0, "200px");
		table4.getColumnFormatter().setWidth(1, "200px");
		table4.getColumnFormatter().setWidth(2, "200px");
		table4.getColumnFormatter().setWidth(3, "200px");

	

		int row = 0;
		table4.setWidget(row, 0, new Label("Fecha"));
		table4.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table4.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table4.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table4.setWidget(row, 1, new Label("Relaci\u00F3n numerada de las acciones / participaciones"));
		table4.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table4.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table4.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table4.setWidget(row, 2, new Label("T\u00EDtulo de adquisici\u00F3n"));
		table4.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table4.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table4.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table4.setWidget(row, 3, new Label("% sobre capital"));
		table4.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table4.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table4.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
	
		++row;
		for(Integer i = 0; i< D2DepositConstants.A4_ABREVIATE_KEYS.length; i+=4){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A4_ABREVIATE_KEYS[i],
					D2DepositConstants.A4_ABREVIATE_KEYS[i+1],
					D2DepositConstants.A4_ABREVIATE_KEYS[i+2],
					D2DepositConstants.A4_ABREVIATE_KEYS[i+3],


			};
			for (Integer j = 0; j < d2.length ;j++) {
				if(j == 1 || j == 2)
					paintTextKeyField(table4, d2[j], row, j);
				else if(j == 0  )
					paintDateKeyField(table4, d2[j], row, j);
				else if(j == 3)
					paintDoubleKeyField(table4 ,d2[j],row,j);
			}
			++row;		
		}

	}
	
	private void table5(){

		table5.setWidth("100%");
		table5.setCellSpacing(0);
		table5.getColumnFormatter().setWidth(0, "200px");
		table5.getColumnFormatter().setWidth(1, "200px");
		table5.getColumnFormatter().setWidth(2, "200px");

	

		int row = 0;
		table5.setWidget(row, 0, new Label("Fecha"));
		table5.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table5.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table5.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table5.setWidget(row, 1, new Label("Descripci\u00F3n del negocio"));
		table5.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table5.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table5.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table5.setWidget(row, 2, new Label("N\u00FAmero de acciones dadas en garant\u00EDa"));
		table5.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table5.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table5.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());


		++row;
		for(Integer i = 0; i< D2DepositConstants.A5_ABREVIATE_KEYS.length; i+=3){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A5_ABREVIATE_KEYS[i],
					D2DepositConstants.A5_ABREVIATE_KEYS[i+1],
					D2DepositConstants.A5_ABREVIATE_KEYS[i+2],

			};
			for (Integer j = 0; j < d2.length ;j++) {
				if(j == 1)
					paintTextKeyField(table5, d2[j], row, j);
				else if(j == 0 )
					paintDateKeyField(table5, d2[j], row, j);
				else if(j == 2)
					paintDoubleKeyField(table5 ,d2[j],row,j);
			}
			++row;		
		}

	}
	
	private void table6(){

		table6.setWidth("100%");
		table6.setCellSpacing(0);
		table6.getColumnFormatter().setWidth(0, "200px");
		table6.getColumnFormatter().setWidth(1, "200px");
		table6.getColumnFormatter().setWidth(2, "200px");

	

		int row = 0;
		table6.setWidget(row, 0, new Label("Fecha"));
		table6.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table6.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table6.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table6.setWidget(row, 1, new Label("Descripci\u00F3n del negocio"));
		table6.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table6.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table6.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table6.setWidget(row, 2, new Label("N\u00FAmero de acciones dadas en garant\u00EDa"));
		table6.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table6.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table6.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());

		++row;
		for(Integer i = 0; i< D2DepositConstants.A6_ABREVIATE_KEYS.length; i+=3){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A6_ABREVIATE_KEYS[i],
					D2DepositConstants.A6_ABREVIATE_KEYS[i+1],
					D2DepositConstants.A6_ABREVIATE_KEYS[i+2],
	

			};
			for (Integer j = 0; j < d2.length ;j++) {
				if(j == 1)
					paintTextKeyField(table6, d2[j], row, j);
				else if(j == 0 )
					paintDateKeyField(table6, d2[j], row, j);
				else if(j == 2)
					paintDoubleKeyField(table6 ,d2[j],row,j);
			}
			++row;		
		}

	}
	
	private void table7(){

		table7.setWidth("100%");
		table7.setCellSpacing(0);
		table7.getColumnFormatter().setWidth(0, "200px");
		table7.getColumnFormatter().setWidth(1, "200px");
		table7.getColumnFormatter().setWidth(2, "200px");
		table7.getColumnFormatter().setWidth(3, "200px");
		table7.getColumnFormatter().setWidth(4, "200px");
	

		int row = 0;
		table7.setWidget(row, 0, new Label("Sociedad Comunicante"));
		table7.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table7.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table7.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table7.setWidget(row, 1, new Label("Fecha Comunicaci\u00F3n"));
		table7.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table7.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table7.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table7.setWidget(row, 2, new Label("Porcentaje de participaci\u00F3n en su capital a esa fecha"));
		table7.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table7.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table7.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table7.setWidget(row, 3, new Label("Fecha Reducci\u00F3n"));
		table7.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table7.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table7.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table7.setWidget(row, 4, new Label("Porcentaje Posterior"));
		table7.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table7.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table7.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());

		++row;
		for(Integer i = 0; i< D2DepositConstants.A1_ABREVIATE_KEYS_1.length; i+=5){
			D2DepositFooterKey[] d2 = new D2DepositFooterKey[]{
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+1],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+2],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+3],
					D2DepositConstants.A1_ABREVIATE_KEYS_1[i+4],

			};
			for (Integer j = 0; j < d2.length ;j++) {
				if(j == 0)
					paintTextKeyField(table7, d2[j], row, j);
				else if(j == 1 || j ==3 )
					paintDateKeyField(table7, d2[j], row, j);
				else if(j == 2 || j == 4)
					paintDoubleKeyField(table7 ,d2[j],row,j);
			}
			++row;		
		}

	}
	
	
	protected int paintKey(FlexTable tab, D2DepositKey[] keys,  int row) {
		for (Integer i = 0; i < keys.length ; i++) {
			paintKeyField(tab,keys[i],row,i+1);
		}
		return  ++row;
	}
	
	private void paintTextKeyField(FlexTable tab, D2DepositFooterKey key,  int row, int col){
		
		boolean disabled = A18009050.getValue();
		
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		
		final TextBox text = new TextBox();
		text.setTitle(codeId);
		text.setStyleName(AON.AON_CSS.aonInputText());
		codeAux = codeId;
		text.addChangeHandler(new ChangeHandler() {
			String code = codeAux;
			@Override
			public void onChange(ChangeEvent event) {
				try {
					if (AonStringUtils.isEmpty(text.getText())) {
						text.setValue("",false);
					}
					String d = text.getValueOrThrow();
					text.addStyleName(AON.AON_CSS.aonChanged());
					text.setTitle(code);
					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);
					onEdit(code, d);
					inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),code, d , new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});
				} catch (ParseException e) {
					// nothing.
				}
				
			}
		});
		if(map.containsKey(key.getName())){
			String d =map.get(key.getName());
			text.setValue(d);
		}
		else text.setValue("");
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		text.setEnabled(!disabled);
		panel.add(text);
		
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}

	private void paintDateKeyField(FlexTable tab, D2DepositFooterKey key,  int row, int col){
		
		boolean disabled = A18009050.getValue();
		
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		
		final DateBox text = new DateBox();
		text.setTitle(codeId);
		text.setStyleName(AON.AON_CSS.aonInputText());
		codeAux = codeId;
		text.addValueChangeHandler(new ValueChangeHandler<Date>() {
			String code = codeAux;

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				
					
					Date d = text.getValue();
					Integer day = d.getDate();
					Integer month = d.getMonth();
					Integer year = d.getYear()+1900;
					String value = day+"."+month+"."+year;
					
					text.addStyleName(AON.AON_CSS.aonChanged());
					text.setTitle(code);
					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);
					onEdit(code, value);
					inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),code, value, new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});
								
			}
		});
		
		if(map.containsKey(key.getName())){
			String datestr = map.get(key.getName());

			inma.getDate(datestr, new AsyncCallback<Date>() {
				@Override
				public void onSuccess(Date result) {
					text.setValue(result); 
					
				}
				
				@Override
				public void onFailure(Throwable caught) {
					
				}
			} );
		}

		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		text.setEnabled(!disabled);
		panel.add(text);
		
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}
	
	private void paintDoubleKeyField(FlexTable tab, D2DepositFooterKey key,  int row, int col){
		
		boolean disabled = A18009050.getValue();
		
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		
		final DoubleBox text = new DoubleBox();
		text.setTitle(codeId);
		codeAux = codeId;
		text.addChangeHandler(new ChangeHandler() {
			String code = codeAux;
			@Override
			public void onChange(ChangeEvent event) {
				try {
					if (AonStringUtils.isEmpty(text.getText())) {
						text.setValue(0.0,false);
					}
					Double d = text.getValueOrThrow();
					text.addStyleName(AON.AON_CSS.aonChanged());
					text.setTitle(code);
					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);
					onEdit(code, d.toString());
					inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),code, d.toString() , new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});
				} catch (ParseException e) {
					// nothing.
				}
				
			}
		});
		if(map.containsKey(key.getName())){
			Double d =Double.parseDouble(map.get(key.getName()));
			text.setValue(d);
		}
		else text.setValue(0.0);
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		text.setEnabled(!disabled);
		panel.add(text);
		
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}
 	
	private void paintListKeyField(FlexTable tab, D2DepositFooterKey key,  int row, int col){
		
		boolean disabled = A18009050.getValue();
		
		FlowPanel panel = new FlowPanel();
		String codeId = key.getCode();
		
		final ListBox text = new ListBox();
		conceptListBox(text);
		codeAux = codeId;
		text.setTitle(codeId);
		text.addChangeHandler(new ChangeHandler() {
			String code = codeAux;
			@Override
			public void onChange(ChangeEvent event) {
		
					if (AonStringUtils.isEmpty(text.getSelectedItemText())) {
						text.setSelectedIndex(0);
					}
					String d = text.getSelectedItemText();
					text.addStyleName(AON.AON_CSS.aonChanged());
					
					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);
					onEdit(code, d.toString());
					inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),code, d.toString() , new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});
			
				
			}
		});
		if(map.containsKey(key.getName())){
			String d = map.get(key.getName());
			for(Integer i = 0; i< text.getItemCount(); i++){
				if(text.getItemText(i).equals(d))
					text.setSelectedIndex(i);
			}
		}
		else text.setSelectedIndex(0);
		text.addStyleName(AON.AON_CSS.aonFiscalMarginLeft());
		text.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());
		text.setEnabled(!disabled);
		panel.add(text);
		
		tab.setWidget(row, col, panel);
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
	}
	
	
	String key2Aux;
	DoubleBox dlAux;
	private void keyExe(Map<String, String> map, String key2, Widget w, String type, Boolean enable) {
		key2Aux = key2;
		if(type.equals("check")) {
			CheckBox c = (CheckBox) w;
			if(map.containsKey(key2)){
				c.setValue(map.get(key2).equals("1")); 
				c.setEnabled(enable);
			}
			c.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				String key2 = key2Aux;
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);	
					onEdit(key2, event.getValue()?"1":"0");
					inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),key2, event.getValue()?"1":"0", new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});					
					if(key2.equals("8009050") ){
			
						if(event.getValue()){
							for(Integer i = 1 ; i < 8 ; i++){
								tabPanel.getWidget(i).setVisible(false);
							}
							resetTables();
						}
						else{
							for(Integer i = 1 ; i < 8 ; i++){
								tabPanel.getWidget(i).setVisible(true);
							}						
						}
					}
				}
			});
		}
		if(type.equals("double")){
			DoubleBox dl = (DoubleBox) w;
			if(map.containsKey(key2)){
				Double d = Double.parseDouble(map.get(key2));
				dl.setValue(d);
				dl.setEnabled(enable);
			}
			dlAux = dl;
			dl.addChangeHandler(new ChangeHandler() {
				String key2 = key2Aux ;
				DoubleBox dl = dlAux;
				@Override
				public void onChange(ChangeEvent event) {

					normalizedMemory.saveButton.setEnabled(true);
					normalizedMemory.cancelButton.setVisible(true);
					onEdit(key2, dl.getValue().toString());
					inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),key2, dl.getValue().toString(), new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});
				}
			});
		}
	}
	
	private void conceptListBox(ListBox lb ){
		lb.addItem("");
		lb.addItem("AO");
		lb.addItem("AD");
		lb.addItem("AI");
		lb.addItem("AL");
		lb.addItem("ED");
		lb.addItem("EL");
		lb.addItem("RD");
		lb.addItem("RL");
		lb.addItem("AG");
		lb.addItem("AG");
		lb.addItem("PR");
	}
	private void resetTables(){
		for (Integer i = 0; i < table.getRowCount(); i++) {
			for(Integer j = 0; j < table.getCellCount(i); j++){
				
				if(j == 0 || j == 2){
					DateBox dateBox= (DateBox)table.getWidget(i, j);
					dateBox.setValue(null);

					inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),dateBox.getTitle(), "", new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});			
					dateBox.setEnabled(false);
				}
				else if(j == 1){
					ListBox listBox = (ListBox) table.getWidget(i, j);
					listBox.setSelectedIndex(0);
					inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),listBox.getTitle(), "", new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});		
					listBox.setEnabled(false);
				}
				else{
					DoubleBox doubleBox = (DoubleBox) table.getWidget(i, j);
					doubleBox.setValue(0.0);
					Double d = 0.0;
					inma.updateSchema(enterprise.getDocument(),enterprise.getDomain(),doubleBox.getTitle(),"" , new AsyncCallback<Void>() {
						@Override
						public void onFailure(Throwable caught) {}
						@Override
						public void onSuccess(Void result) {}
					});		
					doubleBox.setEnabled(false);
				}
					
				
			}
		}
	}
	
	
}
