package net.aonsolutions.aon.gwt.ccaa.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2PDepositConstants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

import net.aonsolutions.aon.gwt.ccaa.client.Deposit2;

public class PageH3 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageH3> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField TabPanel tabPanel;

	public PageH3(Deposit2 deposit) {
		super(deposit);
		initialize(0);
	}
	
	public PageH3(Deposit2 deposit, Integer tab) {
		super(deposit);
		initialize(tab);
	}
	
	private void initialize(Integer tab) {
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(tab != null ? tab : 0);		
		initializeTable();
	}

	@Override
	protected void initializeTable() {
		if (isPymes()) {
			defineBalanceTable(table,AON.MSG.debitCredit(),D2PDepositConstants.PYG_PYMES_KEYS);
		} else {
			defineBalanceTable(table,AON.MSG.debitCredit(),D2DepositConstants.PYG_ABREVIATE_KEYS);
		}
	}
	
	@Override
	protected void refreshDepositPage() {
		getDeposit().refreshPage(tabPanel.getTabBar().getSelectedTab());
	}
}
