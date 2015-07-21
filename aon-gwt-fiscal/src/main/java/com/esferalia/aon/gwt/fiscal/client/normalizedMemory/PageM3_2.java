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

public class PageM3_2 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageM3_2> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table1;


	public PageM3_2() {
		super();
		table1 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public PageM3_2(Enterprise enterprise, NormalizedMemory nm) {
		super();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		table1 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}

	@Override
	protected void initializeTable() {
		
		if (isPymes()) {
			modelRequestTable(table, "Base de Reparto", D2PDepositConstants.MRN_PYMES_KEYS_1);
			modelRequestTable(table1, "Base de Reparto", D2PDepositConstants.MRN_PYMES_KEYS_2);
		}
		else {
			modelRequestTable(table, "Base de Reparto", D2DepositConstants.MRN_ABREVIATE_KEYS_1);
			modelRequestTable(table1, "Base de Reparto", D2DepositConstants.MRN_ABREVIATE_KEYS_2);
		}
		
//		table.setWidth("100%");
//		table.setCellSpacing(0);
//		table.getColumnFormatter().setWidth(1, "200px");
//		table.getColumnFormatter().setWidth(2, "200px");
//		int row = 0;
//		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
//		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
//		table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
//		table.setWidget(row, 1, new Label("Ejercicio 2014"));
//		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
//		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
//		table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
//		table.setWidget(row, 2, new Label("Ejercicio 2013"));
//		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
//		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
//		table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
//		++row;
//		
//		// ***************** PYMES *********************************
//		for (final D2DepositKey key : D2PDepositConstants.MP3_ABREVIATE_KEYS_1) {
//			for (final D2DepositKey key2 : D2PDepositConstants.MP3_ABREVIATE_KEYS_2){
//				if(key2.getName().equals(key.getName()+"9"))
//					row = paintKey(table, key, key2, row);
//			}
//		}
//
//		// ***************** ABREVIADO *********************************
////		for (final D2DepositKey key : D2DepositConstants.MA3_ABREVIATE_KEYS_1) {
////			for (final D2DepositKey key2 : D2DepositConstants.MA3_ABREVIATE_KEYS_2){
////				if(key2.getName().equals(key.getName()+"9"))
////					row = paintKey(table, key, key2, row);
////			}
////		}
//
//		table1.setWidth("100%");
//		table1.setCellSpacing(0);
//		table1.getColumnFormatter().setWidth(1, "200px");
//		table1.getColumnFormatter().setWidth(2, "200px");
//		row = 0;
//		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
//		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
//		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
//		table1.setWidget(row, 1, new Label("Ejercicio 2014"));
//		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
//		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
//		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
//		table1.setWidget(row, 2, new Label("Ejercicio 2013"));
//		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
//		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
//		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
//		
//		++row;
//		
//		//******************** PYMES ***********************
//		for (final D2DepositKey key : D2PDepositConstants.MP3_ABREVIATE_KEYS_3) {
//			for (final D2DepositKey key2 : D2PDepositConstants.MP3_ABREVIATE_KEYS_4) {
//				if(key2.getName().equals(key.getName()+"9"))
//					row = paintKey(table1, key, key2, row);
//			}
//		}
//		
//		//******************** ABREVIADO ***********************
////		for (final D2DepositKey key : D2DepositConstants.MA3_ABREVIATE_KEYS_3) {
////			for (final D2DepositKey key2 : D2DepositConstants.MA3_ABREVIATE_KEYS_4) {
////				if(key2.getName().equals(key.getName()+"9"))
////					row = paintKey(table1, key, key2, row);
////			}
////		}
	}
//	
//
//	protected int paintKey(FlexTable tab, D2DepositKey key1, D2DepositKey key2,  int row) {
//		paintKeyDescription(tab,key1,row,0);
//		paintKeyField(tab,key1,row,1);
//		paintKeyField(tab,key2,row,2);
//		return  ++row;
//	}
	
	protected void modelRequestTable(FlexTable tab, String title, D2DepositKey[][] keys) {
		
		tab.setWidth("100%");
		tab.setCellSpacing(0);
		tab.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonWidthAuto());
		tab.getColumnFormatter().addStyleName(1, AON.AON_CSS.aonWidth130());
		tab.getColumnFormatter().addStyleName(2, AON.AON_CSS.aonWidth140());		
		int row = 0;
		tab.setWidget(row, 0, new Label(title));
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		tab.setWidget(row, 1, new Label(AON.MSG.year2014()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextRight());
		tab.setWidget(row, 2, new Label(AON.MSG.year2013()));
		tab.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextRight());
		++row;
		
		for (D2DepositKey[] innerKeys : keys) {
			row = paint(tab, innerKeys , row);
		}
	}

	protected int paint(FlexTable tab, D2DepositKey[] keys, int row) {
		paintKeyDescription(tab, keys[0], row, 0);
		paintKeyFieldTextBox(tab,keys[0].getCode(), keys[1], row, 1);
		paintKeyField(tab,keys[0],row,1,false);
		paintKeyField(tab,keys[1],row,2,false);
		return  ++row;

		
	}
}
