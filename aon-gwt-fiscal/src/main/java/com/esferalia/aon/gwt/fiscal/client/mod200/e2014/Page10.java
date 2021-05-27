package com.esferalia.aon.gwt.fiscal.client.mod200.e2014;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014BN082Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014BN565Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014BN570Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014BN571Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014BN572Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014BN573Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014BN584Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014BN585Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014BN588Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014BN590Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key;
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
		AON.MSG.pendingDeduction2014(),
		AON.MSG.appliedDeduction(),
		AON.MSG.futureDeduction()};
	
	private static final String[] HEADERS_2 = new String[]{null,
		AON.MSG.pendingDeduction(),
		AON.MSG.pendingDeduction2014(),
		AON.MSG.appliedDeduction(),
		AON.MSG.futureDeduction()};

	private static final String[] HEADERS_3 = new String[]{null,
		AON.MSG.pendingDeduction(),
		AON.MSG.appliedDeduction(),
		AON.MSG.futureDeduction()};
	
	private static final String[] HEADERS_4 = new String[]{null,
		AON.MSG.generatedDeduction(),
		AON.MSG.reducedDeduction(),
		AON.MSG.quotableAmount(),
		AON.MSG.pendingDueToQuota()};
	

	@UiField(provided = true)
	FlexTable table1;
	
	public Page10() {
		super();
		table1 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(1, "200px");
	}
	
	@Override
	protected void initializeTable() {
		int row = 0;
		for (final Mod2002014Key key : Mod2002014Constants.LIQUIDATION_III_KEYS_1) {
			if (mod200Object.isVisible(key)) {
				row = paintKey(table,key,row);
				if (key == Mod2002014Key.BN570) {
//					row = paintKeyBreakdown(table,row,AON.MSG.doubleContributionNationalPrevious()
//							,Mod2002014BN570Key.values(),HEADERS_1);
					row = paintKeyBreakdownLink(table,row,AON.MSG.doubleContributionNationalPrevious()
							,Mod2002014BN570Key.values(),HEADERS_1);
										
				} 
				if (key == Mod2002014Key.BN571) {
					row = paintKeyBreakdownLink(table,row,AON.MSG.doubleContributionNational2014()
							,Mod2002014BN571Key.values(),HEADERS_2);
				} 
				if (key == Mod2002014Key.BN572) {
					row = paintKeyBreakdownLink(table,row,AON.MSG.doubleContributionInternationalPrevious()
							,Mod2002014BN572Key.values(),HEADERS_1);
				} 
				if (key == Mod2002014Key.BN573) {
					row = paintKeyBreakdownLink(table,row,AON.MSG.doubleContributionInternational2014()
							,Mod2002014BN573Key.values(),HEADERS_2);
				}
			}
		}
		row = 0;
		for (final Mod2002014Key key : Mod2002014Constants.LIQUIDATION_III_KEYS_2) {
			row = paintKey(table1,key,row);
			if (mod200Object.isVisible(key)) {
				if (key == Mod2002014Key.BN585) {
					row = paintKeyBreakdownLink(table1,row,Mod2002014Key.BN585.getDescription()
							,Mod2002014BN585Key.values(),HEADERS_3);
				} 
				if (key == Mod2002014Key.BN584) {
					row = paintKeyBreakdownLink(table1,row,Mod2002014Key.BN584.getDescription()
							,Mod2002014BN584Key.values(),HEADERS_3);
				} 
				if (key == Mod2002014Key.BN588) {
					row = paintKeyBreakdownLink(table1,row,Mod2002014Key.BN588.getDescription()
							,Mod2002014BN588Key.values(),HEADERS_3);
				} 
				if (key == Mod2002014Key.BN082) {
					row = paintKeyBreakdownLink(table1,row,Mod2002014Key.BN082.getDescription()
							,Mod2002014BN082Key.values(),HEADERS_4);
				} 
				if (key == Mod2002014Key.BN565) {
					row = paintKeyBreakdownLink(table1,row,Mod2002014Key.BN082.getDescription()
							,Mod2002014BN565Key.values(),HEADERS_3);
				} 
				if (key == Mod2002014Key.BN590) {
					row = paintKeyBreakdownLink(table1,row,Mod2002014Key.BN590.getDescription()
							,Mod2002014BN590Key.values(),HEADERS_3);
				}
			}
		}
	}
	
}
