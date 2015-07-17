package com.esferalia.aon.gwt.fiscal.client.normalizedMemory;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositConstants;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2DepositHeaderKey;
import com.esferalia.aon.occam.api.model.fiscal.d2_deposit.D2PDepositConstants;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;

public class PageH2 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, PageH2> {
	}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table1;
	
	@UiField(provided = true)
	FlexTable table2;
	
	@UiField TabPanel tabPanel;

	public PageH2() {
		super();
		table1 = new FlexTable();
		table2 = new FlexTable();
		tabPanel = new TabPanel();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(0);
	}
	
	public PageH2(Enterprise enterprise, NormalizedMemory nm) {
		super();
		this.enterprise = enterprise;
		this.normalizedMemory = nm;
		table1 = new FlexTable();
		table2 = new FlexTable();
		tabPanel = new TabPanel();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		tabPanel.selectTab(0);
	}

	@Override
	protected void initializeTable() {
		if (isPymes()) {
			defineTable(table,AON.MSG.balanceActivo(),D2PDepositConstants.BP_ABREVIATE_KEYS_1);
			defineTable(table1,AON.MSG.balancePasivo(),D2PDepositConstants.BP_ABREVIATE_KEYS_2);
			defineTable(table2,AON.MSG.balancePasivo(),D2PDepositConstants.BP_ABREVIATE_KEYS_3);
		} else {
			defineTable(table,AON.MSG.balanceActivo(),D2DepositConstants.BA_ABREVIATE_KEYS_1);
			defineTable(table1,AON.MSG.balanceActivo(),D2DepositConstants.BA_ABREVIATE_KEYS_2);
			defineTable(table2,AON.MSG.balanceActivo(),D2DepositConstants.BA_ABREVIATE_KEYS_3);
		}
	}
	private void defineTable( FlexTable tab, String title, D2DepositHeaderKey[][] keys){
		tab.setWidth("100%");
		tab.setCellSpacing(0);
		tab.getColumnFormatter().addStyleName(0, AON.AON_CSS.aonWidthAuto());
		tab.getColumnFormatter().addStyleName(1, AON.AON_CSS.aonWidth150());
		tab.getColumnFormatter().addStyleName(2, AON.AON_CSS.aonWidth150());
		tab.getColumnFormatter().addStyleName(3, AON.AON_CSS.aonWidth150());
		int row = 0;
		tab.setWidget(row, 0, new Label(title));
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		tab.setWidget(row, 1, new Label(AON.MSG.memoryNotes()));
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextLeft());
		tab.setWidget(row, 2, new Label(AON.MSG.year2014()));
		tab.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextRight());
		tab.setWidget(row, 3, new Label(AON.MSG.year2013()));
		tab.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		tab.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		tab.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextRight());
		++row;
		
		for (D2DepositHeaderKey[] innerKeys : keys) {
			row = paintKey(tab, innerKeys , row);
		}
	}
	
	protected int paintKey(FlexTable tab, D2DepositHeaderKey[] keys,  int row) {
		paintKeyDescription(tab, keys[0], row, 0);
		paintKeyFieldTextBox(tab,keys[0].getCode(), keys[2], row, 1);
		paintKeyField(tab,keys[0],row,2,false);
		paintKeyField(tab,keys[1],row,3,false);
		return  ++row;
	}
}
