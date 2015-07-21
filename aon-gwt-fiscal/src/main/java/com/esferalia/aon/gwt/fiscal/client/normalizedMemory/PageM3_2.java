package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2PDepositConstants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class PageM3_2 extends PageAbs {

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
	
	public PageM3_2(Enterprise enterprise, NormalizedMemory nm) {
		this();
		
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
	}

	@Override
	protected void initializeTable() {
		
		tabPanel.selectTab(0);
		
		if (isPymes()) {
			modelRequestTable(table, AON.MSG.distributionBases().toUpperCase(), D2PDepositConstants.MRN_PYMES_KEYS_1);
			modelRequestTable(table1, AON.MSG.aplicationTo().toUpperCase(), D2PDepositConstants.MRN_PYMES_KEYS_2);
		}
		else {
			modelRequestTable(table, AON.MSG.distributionBases().toUpperCase(), D2DepositConstants.MRN_ABREVIATE_KEYS_1);
			modelRequestTable(table1, AON.MSG.aplicationTo().toUpperCase(), D2DepositConstants.MRN_ABREVIATE_KEYS_2);
		}
	}
		
	
	protected void modelRequestTable(FlexTable tab, String title, D2DepositKey[][] keys) {
		
		tab.setWidth("100%");
		tab.setCellSpacing(0);
		tab.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonWidthAuto());
		tab.getColumnFormatter().addStyleName(1, AON.AON_CSS.aonWidth140());
		tab.getColumnFormatter().addStyleName(2, AON.AON_CSS.aonWidth140());		
		int row = 0;
		tab.setWidget(row, 0, new Label(title));
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		tab.setWidget(row, 1, new Label(AON.MSG.year2014()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextRight());
		tab.setWidget(row, 2, new Label(AON.MSG.year2013()));
		tab.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextRight());
		++row;
		
		for (D2DepositKey[] innerKeys : keys) {
			row = paint(tab, innerKeys , row);
		}
	}

	protected int paint(FlexTable tab, D2DepositKey[] keys, int row) {
		paintKeyDescription(tab, keys[0], row, 0);
		paintKeyFieldTextBox(tab,keys[0].getCode(), keys[0], row, 1);
		//paintKeyField(tab,keys[0],row,1,false);
		paintKeyField(tab,keys[1],row,2,false);
		return  ++row;

		
	}
}
