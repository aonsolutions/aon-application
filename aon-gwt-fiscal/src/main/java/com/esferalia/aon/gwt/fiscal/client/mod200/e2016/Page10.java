package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Model2002016.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016BN1280Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016BN1344Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016BN570Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016BN571Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016BN572Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016BN573Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
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
		AON.MSG.pendingDeduction2016(),
		AON.MSG.appliedDeduction(),
		AON.MSG.futureDeduction()};
	
	private static final String[] HEADERS_2 = new String[]{null,
		AON.MSG.pendingDeduction(),
		AON.MSG.appliedDeduction(),
		AON.MSG.futureDeduction()};
	
	public Page10( Model200PageCallback callback ) {
		super(callback);
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
	}
	
	@Override
	protected void initializeTable() {
		int row = 0;
		for (final Mod2002016Key key : Mod2002016Constants.LIQUIDATION_III_KEYS) {
			if (callback.getMod200Object().isVisible(key)) {
				row = paintKey(table,key,row);
				if (key == Mod2002016Key.BN570) {
					row = paintKeyBreakdownLink(table,row,AON.MSG.doubleContributionNationalPrevious()
							,Mod2002016BN570Key.values(),HEADERS_1);
					table.getFlexCellFormatter().addStyleName((row-2), 0, AON.AON_CSS.aonPadding2Left());
				} 
				if (key == Mod2002016Key.BN1344) {
					row = paintKeyBreakdownLink(table,row,AON.MSG.doubleContributionNationalPrevious()
							,Mod2002016BN1344Key.values(),HEADERS_1);
					table.getFlexCellFormatter().addStyleName((row-2), 0, AON.AON_CSS.aonPadding2Left());
					table.getFlexCellFormatter().addStyleName((row-1), 0, AON.AON_CSS.aonPadding2Left());
				}
				if (key == Mod2002016Key.BN1280) {
					row = paintKeyBreakdownLink(table,row,AON.MSG.doubleContributionNationalPrevious()
							,Mod2002016BN1280Key.values(),HEADERS_2);
					table.getFlexCellFormatter().addStyleName((row-1), 0, AON.AON_CSS.aonPadding2Left());
				}
				if (key == Mod2002016Key.BN572) {
					row = paintKeyBreakdownLink(table,row,AON.MSG.doubleContributionInternationalPrevious()
							,Mod2002016BN572Key.values(),HEADERS_1);
					table.getFlexCellFormatter().addStyleName((row-2), 0, AON.AON_CSS.aonPadding2Left());
				} 
				if (key == Mod2002016Key.BN571) {
					row = paintKeyBreakdownLink(table,row,AON.MSG.doubleContributionNational2016()
							,Mod2002016BN571Key.values(),HEADERS_1);
					table.getFlexCellFormatter().addStyleName((row-2), 0, AON.AON_CSS.aonPadding2Left());
				} 
				if (key == Mod2002016Key.BN573) {
					row = paintKeyBreakdownLink(table,row,AON.MSG.doubleContributionInternational2016()
							,Mod2002016BN573Key.values(),HEADERS_2);
					table.getFlexCellFormatter().addStyleName((row-2), 0, AON.AON_CSS.aonPadding2Left());
				}
				if (key == Mod2002016Key.BN575) {
					table.getFlexCellFormatter().addStyleName((row-1), 0, AON.AON_CSS.aonPadding2Left());
				}
				if (key == Mod2002016Key.BN577) {
					table.getFlexCellFormatter().addStyleName((row-1), 0, AON.AON_CSS.aonPadding2Left());
				}
			}
		}
	}
	
	@Override
	protected void populate() {}
}
