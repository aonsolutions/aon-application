package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Model2002016.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016BN082Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016BN1040Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016BN1041Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016BN565Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016BN584Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016BN585Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016BN588Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016BN590Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class Page11 extends PageAbs {

	interface PageBinder extends
			UiBinder<Widget, Page11> {
	}

	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);
	
	private static final String[] HEADERS_1 = new String[]{null,
		AON.MSG.pendingDeduction(),
		AON.MSG.appliedDeduction(),
		AON.MSG.futureDeduction()};
	
	private static final String[] HEADERS_2 = new String[]{null,
		AON.MSG.generatedDeduction(),
		AON.MSG.reducedDeduction(),
		AON.MSG.quotableAmount(),
		AON.MSG.pendingDueToQuota()};
	
	private static final String[] HEADERS_3 = new String[]{null,
			AON.MSG.deductionTaxablebase(),
			AON.MSG.liquiMsg2(),
			AON.MSG.liquiMsg31(),
			AON.MSG.pendingAmount()};

	@UiField(provided = true)
	FlexTable table;

	public Page11( Model200PageCallback callback ) {
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
		for (final Mod2002016Key key : Mod2002016Constants.LIQUIDATION_IV_KEYS) {
			row = paintKey(table,key,row);
			if (callback.getMod200Object().isVisible(key)) {
				if (key == Mod2002016Key.BN585) {
					row = paintKeyBreakdownLink(table,row,Mod2002016Key.BN585.getDescription()
							,Mod2002016BN585Key.values(),HEADERS_1);
				} 
				if (key == Mod2002016Key.BN584) {
					row = paintKeyBreakdownLink(table,row,Mod2002016Key.BN584.getDescription()
							,Mod2002016BN584Key.values(),HEADERS_1);
				} 
				if (key == Mod2002016Key.BN588) {
					row = paintKeyBreakdownLink(table,row,Mod2002016Key.BN588.getDescription()
							,Mod2002016BN588Key.values(),HEADERS_1);
				} 
				if (key == Mod2002016Key.BN082) {
					row = paintKeyBreakdownLink(table,row,Mod2002016Key.BN082.getDescription()
							,Mod2002016BN082Key.values(),HEADERS_2);
				} 
				if (key == Mod2002016Key.BN565) {
					row = paintKeyBreakdownLink(table,row,Mod2002016Key.BN082.getDescription()
							,Mod2002016BN565Key.values(),HEADERS_1);
				} 
				if (key == Mod2002016Key.BN590) {
					row = paintKeyBreakdownLink(table,row,Mod2002016Key.BN590.getDescription()
							,Mod2002016BN590Key.values(),HEADERS_1);
				}
				if (key == Mod2002016Key.BN1040) {
					row = paintKeyBreakdownLink(table,row,Mod2002016Key.BN1040.getDescription()
							,Mod2002016BN1040Key.values(),HEADERS_3);
				}
				if (key == Mod2002016Key.BN1041) {
					row = paintKeyBreakdownLink(table,row,Mod2002016Key.BN1041.getDescription()
							,Mod2002016BN1041Key.values(),HEADERS_3);
				}
			}
		}
	}
	
	@Override
	protected void populate() {}
}
