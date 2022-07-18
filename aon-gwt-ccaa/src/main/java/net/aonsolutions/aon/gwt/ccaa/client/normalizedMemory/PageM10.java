package net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
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

import net.aonsolutions.aon.gwt.ccaa.client.Deposit;
import net.aonsolutions.aon.gwt.ccaa.client.Deposit2;

public class PageM10 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageM10> {
	}
	
	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField TabPanel tabPanel;
	
	public PageM10(Deposit2 deposit) {
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
		
		if (isPymes()) defineMRNTable(table, PERIODS, D2PDepositConstants.MP10_PYMES_KEYS);
		else defineMRNTable(table, PERIODS, D2DepositConstants.MRN10_ABREVIATE_KEYS);
	}
	
	protected void defineMRNTable( FlexTable tab, String[] headers, D2DepositKey[][] keys) {
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
