package com.esferalia.aon.gwt.fiscal.deposit.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2PDepositConstants;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class PageM7_2 extends PageAbs {
	
	private static final String[] MRN_HEADER_1 = new String[] {
		AON.MSG.memory7_2Header1()
		,AON.MSG.memory7_2Header2()
		,AON.MSG.memory7_2Header3()
		,AON.MSG.mod202Correction4()
	};
	
	private static final String[] MRN_HEADER_2 = new String[] {
		AON.MSG.memory7_2One()
		, AON.MSG.memory7_2Two()
		, AON.MSG.memory7_2Three()
		, AON.MSG.memory7_2Four()
		, AON.MSG.memory7_2Five()
		, AON.MSG.memory7_2MoreFive()
		, AON.MSG.mod202Correction4()
	};
	
	private static final String[] MRN_HEADER_3 = new String[] {
		AON.MSG.memory7_2Header4()
		, AON.MSG.memory7_2Header5()
		, AON.MSG.memory7_2Header6()
	};
	
	private static final String[] AUXILIARES2014 = new String[] {
		AON.MSG.year2014() , AON.MSG.year2013()
	};
	
	private static final String[] AUXILIARES2015 = new String[] {
		AON.MSG.year2015() , AON.MSG.year2014()
	};
 

	interface PageBinder extends UiBinder<Widget, PageM7_2> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table1;
	
	@UiField(provided = true)
	FlexTable table2;

	@UiField(provided = true)
	FlexTable table3;
	
	@UiField
	TabPanel tabPanel;
	
	@UiField
	InlineLabel table4Label;
	

	public PageM7_2() {
		super();
		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	public PageM7_2(Enterprise enterprise, NormalizedMemory nm, Integer year) {
		super();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		this.year = year;
		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}

	@Override
	protected void initializeTable() {
		/*if(tabPanel.getTabBar().getSelectedTab() != 0)
			tabPanel.selectTab(tabPanel.getTabBar().getSelectedTab());
		else*/
		tabPanel.selectTab(0);
		switch (year) {
		case 2014:
			if (isPymes()) {
				defineMRNTable(table, MRN_HEADER_1, AUXILIARES2014, D2PDepositConstants.MRN7_PYMES_KEYS_1, 2);
				defineMRNTable(table1, MRN_HEADER_1, AUXILIARES2014, D2PDepositConstants.MRN7_PYMES_KEYS_2, 2);
				defineMRNTable(table2, MRN_HEADER_2, null, D2PDepositConstants.MRN7_PYMES_KEYS_3, 1);
				table4Label.setText("");
			
			} else {
				defineMRNTable(table, MRN_HEADER_1, AUXILIARES2014, D2DepositConstants.MRN7_ABREVIATE_KEYS_1, 2);
				defineMRNTable(table1, MRN_HEADER_1, AUXILIARES2014, D2DepositConstants.MRN7_ABREVIATE_KEYS_2, 2);
				defineMRNTable(table2, MRN_HEADER_2, null, D2DepositConstants.MRN7_ABREVIATE_KEYS_3, 1);
				defineMRNTable(table3, MRN_HEADER_3, null, D2DepositConstants.MRN7_ABREVIATE_KEYS_4, 1);
			}
			break;
		case 2015:
			if (isPymes()) {
				defineMRNTable(table, MRN_HEADER_1, AUXILIARES2015, D2PDepositConstants.MRN7_PYMES_KEYS_1, 2);
				defineMRNTable(table1, MRN_HEADER_1, AUXILIARES2015, D2PDepositConstants.MRN7_PYMES_KEYS_2, 2);
				defineMRNTable(table2, MRN_HEADER_2, null, D2PDepositConstants.MRN7_PYMES_KEYS_3, 1);
				table4Label.setText("");
			
			} else {
				defineMRNTable(table, MRN_HEADER_1, AUXILIARES2015, D2DepositConstants.MRN7_ABREVIATE_KEYS_1, 2);
				defineMRNTable(table1, MRN_HEADER_1, AUXILIARES2015, D2DepositConstants.MRN7_ABREVIATE_KEYS_2, 2);
				defineMRNTable(table2, MRN_HEADER_2, null, D2DepositConstants.MRN7_ABREVIATE_KEYS_3, 1);
				defineMRNTable(table3, MRN_HEADER_3, null, D2DepositConstants.MRN7_ABREVIATE_KEYS_4, 1);
			}
			break;
		default:
			break;
		}
		
	}
	
	protected void defineMRNTable (FlexTable tab, String[] headers, String[] footers, D2DepositKey[][] keys, int colSpan) {
		
		tab.setWidth("100%");
		tab.setCellSpacing(0);
		int row = 0;
		int col = 0;
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidthAuto());
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonNowrap());
		col++;
		
		for (String primary : headers) {
			
			tab.getFlexCellFormatter().setColSpan(row, col, colSpan);
			
			tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidth140());
			tab.setWidget(row, col, new Label(primary));	
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
			++col;
		}
		
		if (footers != null) {
			
			++row;
			col = 1;
			
			for (int x = 0; x < headers.length; x++) {
				
				for (String secundary : footers) {

					tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidth140());
					tab.setWidget(row, col, new Label(secundary));	
					tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
					tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
					tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
					++col;
				}
			}
		}
		
		++row;
		col = 0;
		
		for (D2DepositKey[] innerKeys : keys) {
			col = 0;
			paintKeyDescription(tab, innerKeys[0], row, col);
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonNowrap());
			col++;
			for (D2DepositKey key : innerKeys ) {
				
				paintKeyField(tab,key,row,col,(col==1), AonStringUtils.substring(key.getCode(), 0, 4)); 
				col++;
			}
			++row;
		}
	}
}
