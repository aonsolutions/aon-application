package com.esferalia.aon.gwt.fiscal.client.mod200;

import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.MSG;

import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN082Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN565Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN570Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN571Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN572Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN573Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN584Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN585Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN588Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BN590Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Constants;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
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
			MSG.pendingDeduction(),
			MSG.taxType(),
			MSG.pendingDeduction2013(),
			MSG.appliedDeduction(),
			MSG.futureDeduction()};
	
	private static final String[] HEADERS_2 = new String[]{null,
			MSG.pendingDeduction(),
			MSG.pendingDeduction2013(),
			MSG.appliedDeduction(),
			MSG.futureDeduction()};

	private static final String[] HEADERS_3 = new String[]{null,
			MSG.pendingDeduction(),
			MSG.appliedDeduction(),
			MSG.futureDeduction()};
	
	private static final String[] HEADERS_4 = new String[]{null,
			MSG.generatedDeduction(),
			MSG.reducedDeduction(),
			MSG.quotableAmount(),
			MSG.pendingDueToQuota()};
	

	@UiField(provided = true)
	FlexTable table1;
	
	public Page10() {
		super();
		table1 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "250px");
		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(0, "auto");
		table1.getColumnFormatter().setWidth(1, "250px");
	}
	
	@Override
	protected void initializeTable() {
		int row = 0;
		for (final Mod200Key key : Mod200Constants.LIQUIDATION_III_KEYS_1) {
			row = paintKey(table,key,row);
			if (key == Mod200Key.BN570) {
				row = paintKeyBreakdown(table,row,MSG.doubleContributionNationalPrevious()
						,Mod200BN570Key.values(),HEADERS_1);
			} 
			if (key == Mod200Key.BN571) {
				row = paintKeyBreakdown(table,row,MSG.doubleContributionNational2013()
						,Mod200BN571Key.values(),HEADERS_2);
			} 
			if (key == Mod200Key.BN572) {
				row = paintKeyBreakdown(table,row,MSG.doubleContributionInternationalPrevious()
						,Mod200BN572Key.values(),HEADERS_1);
			} 
			if (key == Mod200Key.BN573) {
				row = paintKeyBreakdown(table,row,MSG.doubleContributionInternational2013()
						,Mod200BN573Key.values(),HEADERS_2);
			} 
		}
		row = 0;
		for (final Mod200Key key : Mod200Constants.LIQUIDATION_III_KEYS_2) {
			row = paintKey(table1,key,row);
			if (key == Mod200Key.BN585) {
				row = paintKeyBreakdown(table1,row,Mod200Key.BN585.getDescription()
						,Mod200BN585Key.values(),HEADERS_3);
			} 
			if (key == Mod200Key.BN584) {
				row = paintKeyBreakdown(table1,row,Mod200Key.BN584.getDescription()
						,Mod200BN584Key.values(),HEADERS_3);
			} 
			if (key == Mod200Key.BN588) {
				row = paintKeyBreakdown(table1,row,Mod200Key.BN588.getDescription()
						,Mod200BN588Key.values(),HEADERS_3);
			} 
			if (key == Mod200Key.BN082) {
				row = paintKeyBreakdown(table1,row,Mod200Key.BN082.getDescription()
						,Mod200BN082Key.values(),HEADERS_4);
			} 
			if (key == Mod200Key.BN565) {
				row = paintKeyBreakdown(table1,row,Mod200Key.BN082.getDescription()
						,Mod200BN565Key.values(),HEADERS_3);
			} 
			if (key == Mod200Key.BN590) {
				row = paintKeyBreakdown(table1,row,Mod200Key.BN590.getDescription()
						,Mod200BN590Key.values(),HEADERS_3);
			} 
		}
	}
	
}
