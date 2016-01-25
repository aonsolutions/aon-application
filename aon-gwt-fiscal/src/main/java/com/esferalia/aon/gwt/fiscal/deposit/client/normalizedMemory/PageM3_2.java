package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

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

public class PageM3_2 extends PageAbs {
	
	private static final String[] MRN_HEADER_1_2014 = new String[] {
		AON.MSG.distributionBases().toUpperCase(), 
		AON.MSG.year2014() , AON.MSG.year2013()
	};
	
	private static final String[] MRN_HEADER_2_2014 = new String[] {
		AON.MSG.aplicationTo().toUpperCase(), 
		AON.MSG.year2014() , AON.MSG.year2013()
	};

	private static final String[] MRN_HEADER_1_2015 = new String[] {
		AON.MSG.distributionBases().toUpperCase(), 
		AON.MSG.year2015() , AON.MSG.year2014()
	};
	
	private static final String[] MRN_HEADER_2_2015 = new String[] {
		AON.MSG.aplicationTo().toUpperCase(), 
		AON.MSG.year2015() , AON.MSG.year2014()
	};
	
	interface PageBinder extends UiBinder<Widget, PageM3_2> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField
	TabPanel tabPanel;
	@UiField(provided = true)
	FlexTable table1;
	public PageM3_2() {
		super();
		table1 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public PageM3_2(Enterprise enterprise, NormalizedMemory nm, Integer year) {
		this();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		this.year = year;
	}

	@Override
	protected void initializeTable() {
		tabPanel.selectTab(0);
		switch (year) {
		case 2014:
			if (isPymes()) {
				defineMRNTable(table, MRN_HEADER_1_2014, D2PDepositConstants.MRN_PYMES_KEYS_1);
				defineMRNTable(table1, MRN_HEADER_2_2014, D2PDepositConstants.MRN_PYMES_KEYS_2);
			}
			else {
				defineMRNTable(table, MRN_HEADER_1_2014, D2DepositConstants.MRN_ABREVIATE_KEYS_1);
				defineMRNTable(table1, MRN_HEADER_2_2014, D2DepositConstants.MRN_ABREVIATE_KEYS_2);
			}
			break;
		case 2015:
			if (isPymes()) {
				defineMRNTable(table, MRN_HEADER_1_2015, D2PDepositConstants.MRN_PYMES_KEYS_1);
				defineMRNTable(table1, MRN_HEADER_2_2015, D2PDepositConstants.MRN_PYMES_KEYS_2);
			}
			else {
				defineMRNTable(table, MRN_HEADER_1_2015, D2DepositConstants.MRN_ABREVIATE_KEYS_1);
				defineMRNTable(table1, MRN_HEADER_2_2015, D2DepositConstants.MRN_ABREVIATE_KEYS_2);
			}
			break;
		default:
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
		
		for (String primary : headers) {
			tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidth140());
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
