package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class PageM13_2 extends PageAbs {

	private static final String[] PERIODS2014 = new String[] {
		AON.MSG.year2014() , AON.MSG.year2013() 
	};


	private static final String[] PERIODS2015 = new String[] {
		AON.MSG.year2015() , AON.MSG.year2014() 
	};

	
	interface PageBinder extends UiBinder<Widget, PageM13_2> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField
	TabPanel tabPanel;
	
	

	public PageM13_2() {
		super();

		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public PageM13_2(Enterprise enterprise, NormalizedMemory nm, Integer year) {
		super();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		this.year = year;
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}

	@Override
	protected void initializeTable() {
		tabPanel.selectTab(0);
	
		switch (year) {
		case 2014:
			defineMRNTable(table, PERIODS2014, D2DepositConstants.MRN13_ABREVIATE_KEYS);
			break;
		case 2015:
			defineMRNTable(table, PERIODS2015, D2DepositConstants.MRN13_ABREVIATE_KEYS);
			break;
		default:
			defineMRNTable(table, PERIODS2014, D2DepositConstants.MRN13_ABREVIATE_KEYS);
			break;
		}
		
		
	}
	
	protected void defineMRNTable( FlexTable tab, String[] headers, D2DepositKey[][] keys){
		
		tab.setWidth("100%");
		tab.setCellSpacing(0);
		int row = 0;
		int col = 0;
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidthAuto());
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonNowrap());
		col++;
		
		for (String primary : headers) {
			
			tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidth140());
			tab.setWidget(row, col, new Label(primary));	
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextRight());
			++col;
		}
		
		++row;
		col = 0;
		
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
