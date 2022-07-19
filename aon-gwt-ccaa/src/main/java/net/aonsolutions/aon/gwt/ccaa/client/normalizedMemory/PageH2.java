package net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2PDepositConstants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.aon.gwt.ccaa.client.Deposit;
import net.aonsolutions.aon.gwt.ccaa.client.Deposit2;

public class PageH2 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageH2> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField FlexTable table1;
	@UiField FlexTable table2;
	@UiField TabPanel tabPanel;
	
	public PageH2(Deposit2 deposit) {
		super(deposit);
		
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(0);
		initializeTable();
	}

	@Override
	protected void initializeTable() {
		if (isPymes()) {
			defineBalanceTable(table,AON.MSG.balanceActivo(),D2PDepositConstants.BALANCE_ACTIVE_PYMES_KEYS);
			if(getYear() >= 2015)
				defineBalanceTable(table1,AON.MSG.balancePasivo(),D2PDepositConstants.BALANCE_PASIVE_PYMES_KEYS_1_2015);
			else defineBalanceTable(table1,AON.MSG.balancePasivo(),D2PDepositConstants.BALANCE_PASIVE_PYMES_KEYS_1);
			defineBalanceTable(table2,AON.MSG.balancePasivo(),D2PDepositConstants.BALANCE_PASIVE_PYMES_KEYS_2);
		} else {
			defineBalanceTable(table,AON.MSG.balanceActivo(),D2DepositConstants.BA_ABREVIATE_KEYS_1);
			if(getYear() >= 2015)
				defineBalanceTable(table1,AON.MSG.balancePasivo(),D2DepositConstants.BA_ABREVIATE_KEYS_2_2015);
			else defineBalanceTable(table1,AON.MSG.balanceActivo(),D2DepositConstants.BA_ABREVIATE_KEYS_2);
			defineBalanceTable(table2,AON.MSG.balanceActivo(),D2DepositConstants.BA_ABREVIATE_KEYS_3);
		}
	}
}
