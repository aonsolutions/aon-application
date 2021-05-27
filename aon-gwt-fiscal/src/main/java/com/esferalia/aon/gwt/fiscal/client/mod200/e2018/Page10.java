package com.esferalia.aon.gwt.fiscal.client.mod200.e2018;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2018.Model2002018.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN1280Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN1344Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN570Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN571Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN572Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018BN573Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class Page10 extends PageAbs {

	interface PageBinder extends
			UiBinder<Widget, Page10> {
	}

	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);
	
	private static final String[] HEADERS_1 = new String[]{null,
		AON.MSG.pendingDeduction(),
		AON.MSG.taxType(),
		"2018 deducci\u00F3n pendiente",/*********/
		AON.MSG.appliedDeduction(),
		AON.MSG.futureDeduction()};
	
	private static final String[] HEADERS_2 = new String[]{null,
		AON.MSG.pendingDeduction(),
		AON.MSG.appliedDeduction(),
		AON.MSG.futureDeduction()};
	
	@UiField(provided = true)
	FlexTable table;

	public Page10( Model200PageCallback callback ) {
		super(callback);
		table = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		initializeTable();
	}
	
	@Override
	protected void initializeTable() {
		int row = 0;
		boolean margin = false;
		for (final Mod2002018Key key : Mod2002018Constants.LIQUIDATION_III_KEYS) {
			if (callback.getMod200Object().isVisible(key)) {
				
				if (key == Mod2002018Key.BN570) {
					paintDescription(table, "Deducciones por doble imposici\u00F3n:", row, 0, false);
					row++;
					margin = true;
				}
				
				if (key == Mod2002018Key.BN581) {
					margin = false;
				}
				
				row = paintKey(table,key,row);
				
				if (margin) {
					table.getCellFormatter().addStyleName(row-1, 0, AON.AON_CSS.aonPaddingLeft());
				}				
				
				if (key == Mod2002018Key.BN570) {					
					row = paintKeyBreakdownLink(table,row,Mod2002018Key.BN570,Mod2002018BN570Key.values(),HEADERS_1);
					//table.getFlexCellFormatter().addStyleName((row-2), 0, AON.AON_CSS.aonPadding2Left());
				} 
				if (key == Mod2002018Key.BN1344) {
					row = paintKeyBreakdownLink(table,row,Mod2002018Key.BN1344,Mod2002018BN1344Key.values(),HEADERS_1);
					//table.getFlexCellFormatter().addStyleName((row-2), 0, AON.AON_CSS.aonPadding2Left());
					//table.getFlexCellFormatter().addStyleName((row-1), 0, AON.AON_CSS.aonPadding2Left());
				}
				if (key == Mod2002018Key.BN1280) {
					row = paintKeyBreakdownLink(table,row,Mod2002018Key.BN1280,Mod2002018BN1280Key.values(),HEADERS_2);
					//table.getFlexCellFormatter().addStyleName((row-1), 0, AON.AON_CSS.aonPadding2Left());
				}
				if (key == Mod2002018Key.BN572) {
					row = paintKeyBreakdownLink(table,row,Mod2002018Key.BN572,Mod2002018BN572Key.values(),HEADERS_1);
					//table.getFlexCellFormatter().addStyleName((row-2), 0, AON.AON_CSS.aonPadding2Left());
				} 
				if (key == Mod2002018Key.BN571) {
					row = paintKeyBreakdownLink(table,row,Mod2002018Key.BN571,Mod2002018BN571Key.values(),HEADERS_1);
					//table.getFlexCellFormatter().addStyleName((row-2), 0, AON.AON_CSS.aonPadding2Left());
				} 
				if (key == Mod2002018Key.BN573) {
					row = paintKeyBreakdownLink(table,row,Mod2002018Key.BN573,Mod2002018BN573Key.values(),HEADERS_2);
					//table.getFlexCellFormatter().addStyleName((row-2), 0, AON.AON_CSS.aonPadding2Left());
				}
				if (key == Mod2002018Key.BN575) {
					//table.getFlexCellFormatter().addStyleName((row-1), 0, AON.AON_CSS.aonPadding2Left());
				}
				if (key == Mod2002018Key.BN577) {
					//table.getFlexCellFormatter().addStyleName((row-1), 0, AON.AON_CSS.aonPadding2Left());
				}
				if (margin) {
					table.getCellFormatter().addStyleName(row-1, 0, AON.AON_CSS.aonPaddingLeft());
				}
			}
		}
	}
	
	@Override
	protected boolean isDisabled(Mod2002018Key key) {
		if (key == Mod2002018Key.BN575)  {
			return callback.getMod200Object().getMod200().isNotChecked(Mod2002018Key.C0007);
		}
		return super.isDisabled(key);
	}
	
	@Override
	protected void populate() {}
}
