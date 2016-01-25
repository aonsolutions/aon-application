package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class PageM5_2 extends PageAbs {
	
	private static final String[] MRN_HEADER_A = new String[] {
		"Inmovilizado Intangible 01"
		,"Inmovilizado Material 02"
		,"Inversiones Inmobiliarias 03"
	};
	
	private static final String[] MRN_HEADER_B = new String[] {
		"Inmovilizado Intangible 19"
		,"Inmovilizado Material 29"
		,"Inversiones Inmobiliarias 39"
	};

	
	private static final String[] MRN_HEADER_C = new String[] {
		"Total Contratos"};

	interface PageBinder extends UiBinder<Widget, PageM5_2> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField
	TabPanel tabPanel;
	@UiField(provided = true)
	FlexTable table1;
	
	@UiField(provided = true)
	FlexTable table2;

	public PageM5_2() {
		super();
		table1 = new FlexTable();
		table2 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public PageM5_2(Enterprise enterprise, NormalizedMemory nm, Integer year) {
		super(year);
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		table1 = new FlexTable();
		table2 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}

	@Override
	protected void initializeTable() {
		
		tabPanel.selectTab(0);
		
		defineMRNTable(table, MRN_HEADER_A, D2DepositConstants.MRN5_ABREVIATE_PYMES_KEYS_1);
		defineMRNTable(table1, MRN_HEADER_B, D2DepositConstants.MRN5_ABREVIATE_PYMES_KEYS_2);
		defineMRNTable(table2, MRN_HEADER_C, D2DepositConstants.MRN5_ABREVIATE_PYMES_KEYS_3);
		
	}
	
	protected void defineMRNTable(FlexTable tab, String[] headers, D2DepositKey[][] keys) {
		tab.setWidth("100%");
		tab.setCellSpacing(0);
		int row = 0;
		int col = 0;
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidthAuto());
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonNowrap());
		col++;
		for (String header : headers) {
			tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidth140());
			tab.setWidget(row, col, new Label(header));	
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
				
				paintKeyField(tab,key,row,col,(col==1), (tab == table2) ? key.getCode() : AonStringUtils.substring(key.getCode(), 0, 4));
				
				//paintKeyField(tab,key,row,col,(col==1), AonStringUtils.substring(key.getCode(), 0, 4)); 
				col++;
			}
			++row;
		}
	}
}
