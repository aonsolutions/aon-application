package com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.Deposit;
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

public class PageM3_2 extends PageAbs {
	
	interface PageBinder extends UiBinder<Widget, PageM3_2> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField TabPanel tabPanel;
	@UiField(provided = true) FlexTable table1;
	@UiField(provided = true) FlexTable table2;
		
	public PageM3_2(Deposit deposit) {
		super(deposit);

		table1 = new FlexTable();
		table2 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		
		initializeTable();
	}

	@Override
	protected void initializeTable() {
		String[] MRN_HEADER_1 = new String[] {
			AON.MSG.distributionBases().toUpperCase(), 
			AON.MSG.fiscalYear() + " " + getYear() ,
			AON.MSG.fiscalYear() + " " + (getYear() -1)
		};
		
		String[] MRN_HEADER_2 = new String[] {
			AON.MSG.aplicationTo().toUpperCase(), 
			AON.MSG.fiscalYear() + " " + getYear() ,
			AON.MSG.fiscalYear() + " " + (getYear() -1)
		};
		
		String[] MRN_HEADER_3 = new String[] {
			"Informaci\u00f3n sobre el per\u00edodo medio de pago a proveedores durante el ejercicio".toUpperCase(), 
			AON.MSG.fiscalYear() + " " + getYear() ,
			AON.MSG.fiscalYear() + " " + (getYear() -1)
		};
		tabPanel.selectTab(0);
		
		if (isPymes()) {
			defineMRNTable(table, MRN_HEADER_1, D2PDepositConstants.MRN_PYMES_KEYS_1);
			defineMRNTable(table1, MRN_HEADER_2, D2PDepositConstants.MRN_PYMES_KEYS_2);
		} else {
			defineMRNTable(table, MRN_HEADER_1, D2DepositConstants.MRN_ABREVIATE_KEYS_1);
			defineMRNTable(table1, MRN_HEADER_2, D2DepositConstants.MRN_ABREVIATE_KEYS_2);
		}		
		if(getYear() >= 2016){
			defineMRNTable(table2, MRN_HEADER_3, D2DepositConstants.MRN_ABREVIATE_KEYS_3);
		} else table2.setVisible(false);
	}
	
	protected void defineMRNTable( FlexTable tab, String[] headers, D2DepositKey[][] keys){
		tab.setCellSpacing(0);
		int row = 0;
		int col = 0;
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidthAll());
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonNowrap());		
		
		for (String primary : headers) {
			if(col > 0){
				tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidth140());
			}
			tab.setWidget(row, col, new Label(primary));	
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
			++col;
		}
		
		++row;
		
		for (D2DepositKey[] innerKeys : keys) {
			col = 0;
			paintKeyDescription(tab, innerKeys[0], row, col);
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
			col++;
			for (D2DepositKey key : innerKeys ) {
				
				paintKeyField(tab,key,row,col,(col==1), key.getCode()); 
				col++;
			}
			++row;
		}
	}
}
