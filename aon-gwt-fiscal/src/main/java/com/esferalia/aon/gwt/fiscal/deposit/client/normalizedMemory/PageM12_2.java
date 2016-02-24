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

public class PageM12_2 extends PageAbs {
	
	private static final String[] MRN_HEADERS = new String[] {
		  AON.MSG.memory12_2Header1()
		, AON.MSG.memory12_2Header2()
		, AON.MSG.memory12_2Header3()
		, AON.MSG.memory12_2Header4()
		, AON.MSG.memory12_2Header5()
		, AON.MSG.memory12_2Header6()
		, AON.MSG.memory12_2Header7()
	};
	
	private static final String[] PERIODS2014 = new String[] {
		AON.MSG.year2014(), AON.MSG.year2013()
	};
	private static final String[] PERIODS2015 = new String[] {
		AON.MSG.year2015(), AON.MSG.year2014()
	};

	interface PageBinder extends UiBinder<Widget, PageM12_2> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);
	
	@UiField InlineLabel tableTitle;
	@UiField InlineLabel table1Title;
	@UiField InlineLabel table2Title;
	@UiField InlineLabel table3Title;
	
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
	
	public PageM12_2(Enterprise enterprise, NormalizedMemory nm, Integer year) {
		this();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		this.year = year;
	}

	@Override
	protected void initializeTable() {
		tableTitle.setText(AON.MSG.memory12_1_2X(year));
		table1Title.setText(AON.MSG.memory12_1_2X(year-1));
		table2Title.setText(AON.MSG.memory12_3_4X(year));
		table3Title.setText(AON.MSG.memory12_3_4X(year-1));
		
		if(tabPanel.getTabBar().getSelectedTab() != 0)
			tabPanel.selectTab(tabPanel.getTabBar().getSelectedTab());
		else tabPanel.selectTab(0);
		
		defineMRNTable(table, MRN_HEADERS, D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_1);
		defineMRNTable(table1, MRN_HEADERS, D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_2);
		//defineMRNTable(table3, MRN_HEADERS, D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_4);
		
		switch (year) {
		case 2014:
			if (isPymes()) {
				defineMRNTable(table2, MRN_HEADERS, D2PDepositConstants.MRN12_PYMES_KEYS_3);
				defineMRNTable(table3, MRN_HEADERS, D2PDepositConstants.MRN12_PYMES_KEYS_4);
				defineMRNTable(table4, PERIODS2014, D2PDepositConstants.MRN12_PYMES_KEYS_5);
				defineMRNTable(table5, PERIODS2014, D2PDepositConstants.MRN12_PYMES_KEYS_6);
			} else {
				defineMRNTable(table2, MRN_HEADERS, D2DepositConstants.MRN12_ABREVIATE_KEYS_3);
				defineMRNTable(table3, MRN_HEADERS, D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_4);
				defineMRNTable(table4, PERIODS2014, D2DepositConstants.MRN12_ABREVIATE_KEYS_5);
				defineMRNTable(table5, PERIODS2014, D2DepositConstants.MRN12_ABREVIATE_KEYS_6);
			}
			break;
		case 2015:
			if (isPymes()) {
				defineMRNTable(table2, MRN_HEADERS, D2PDepositConstants.MRN12_PYMES_KEYS_3);
				defineMRNTable(table3, MRN_HEADERS, D2PDepositConstants.MRN12_PYMES_KEYS_4);
				defineMRNTable(table4, PERIODS2015, D2PDepositConstants.MRN12_PYMES_KEYS_5);
				defineMRNTable(table5, PERIODS2015, D2PDepositConstants.MRN12_PYMES_KEYS_6);
			} else {
				defineMRNTable(table2, MRN_HEADERS, D2DepositConstants.MRN12_ABREVIATE_KEYS_3);
				defineMRNTable(table3, MRN_HEADERS, D2DepositConstants.MRN12_ABREVIATE_PYMES_KEYS_4);
				defineMRNTable(table4, PERIODS2015, D2DepositConstants.MRN12_ABREVIATE_KEYS_5);
				defineMRNTable(table5, PERIODS2015, D2DepositConstants.MRN12_ABREVIATE_KEYS_6);
			}
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
		col++;
		for (String primary : headers) {
			
			tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidth140());
			tab.setWidget(row, col, new Label(primary));	
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
			tab.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
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
				
				paintKeyField(tab,key,row,col,(col==1), (tab == table4 || tab == table5) ? AonStringUtils.substring(key.getCode(), 0, 5)
						: AonStringUtils.substring(key.getCode(), 0, 4)); 
				col++;
			}
			++row;
		}
	}
}
