package com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.deposit.client.nuevo.Deposit;
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

public class PageM15 extends PageAbs {

	private static final String[] MRN_FOOTERS = new String[] {
			AON.MSG.amount(), AON.MSG.memory15_2Header1() };

	interface PageBinder extends UiBinder<Widget, PageM15> {
	}
	
	@UiField TabPanel tabPanel;

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	public PageM15(Deposit deposit) {
		super(deposit);

		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		
		initializeTable();
	}

	@Override
	protected void initializeTable() {
		String[] PERIODS = new String[] {
			AON.MSG.fiscalYear() + " " + getYear(),
			AON.MSG.fiscalYear() + " " + (getYear() - 1)
		};
		tabPanel.selectTab(0);
		
		if(getYear().equals(2014)){
			defineMRNTable(table, PERIODS, MRN_FOOTERS, D2DepositConstants.MRN15_ABREVIATE_PYMES_KEYS, 2);
		} else {
			defineMRNTable2(table, PERIODS, D2DepositConstants.MRN15_ABREVIATE_PYMES_KEYS_2015);
		}
	}

	protected void defineMRNTable(FlexTable tab, String[] headers,
			String[] footers, D2DepositKey[][] keys, int colSpan) {

		tab.setWidth("100%");
		tab.setCellSpacing(0);
		int row = 0;
		int col = 0;
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonWidthAuto());
		tab.getColumnFormatter().addStyleName(col, AON.AON_CSS.aonNowrap());
		col++;

		for (String primary : headers) {

			tab.getFlexCellFormatter().setColSpan(row, col, colSpan);

			tab.getColumnFormatter().addStyleName(col,
					AON.AON_CSS.aonWidth140());
			tab.setWidget(row, col, new Label(primary));
			tab.getFlexCellFormatter().addStyleName(row, col,
					AON.AON_CSS.aonBold());
			tab.getFlexCellFormatter().addStyleName(row, col,
					AON.AON_CSS.aonBorderBottom());
			tab.getFlexCellFormatter().addStyleName(row, col,
					AON.AON_CSS.aonTextCenter());
			++col;
		}

		++row;
		col = 1;

		for (int x = 0; x < headers.length; x++) {

			for (String secundary : footers) {

				tab.getColumnFormatter().addStyleName(col,
						AON.AON_CSS.aonWidth140());
				tab.setWidget(row, col, new Label(secundary));
				tab.getFlexCellFormatter().addStyleName(row, col,
						AON.AON_CSS.aonBold());
				tab.getFlexCellFormatter().addStyleName(row, col,
						AON.AON_CSS.aonBorderBottom());
				tab.getFlexCellFormatter().addStyleName(row, col,
						AON.AON_CSS.aonTextCenter());
				++col;
			}
		}

		++row;
		col = 0;
		
		tab.setWidget(row, col, new Label(AON.MSG.memory15_2Row1()));
		
		++row;

		for (D2DepositKey[] innerKeys : keys) {
			col = 0;
			paintKeyDescription(tab, innerKeys[0], row, col);
			tab.getFlexCellFormatter().addStyleName(row, col,
					AON.AON_CSS.aonNowrap());
			col++;
			for (D2DepositKey key : innerKeys) {

				paintKeyField(tab, key, row, col, (col == 1),
						AonStringUtils.substring(key.getCode(), 0, 5));
				col++;
			}
			++row;
		}
	}
	
	protected void defineMRNTable2( FlexTable tab, String[] headers, D2DepositKey[][] keys){
		
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
