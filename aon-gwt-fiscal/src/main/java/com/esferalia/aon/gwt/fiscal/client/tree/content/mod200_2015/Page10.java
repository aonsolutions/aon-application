package com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015BN082Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015BN1040Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015BN1041Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015BN565Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015BN570Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015BN571Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015BN572Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015BN573Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015BN584Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015BN585Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015BN588Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015BN590Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key;
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
		AON.MSG.pendingDeduction2015(),
		AON.MSG.appliedDeduction(),
		AON.MSG.futureDeduction()};
	
	private static final String[] HEADERS_2 = new String[]{null,
		AON.MSG.pendingDeduction(),
		AON.MSG.appliedDeduction(),
		AON.MSG.futureDeduction()};
	
	private static final String[] HEADERS_3 = new String[]{null,
		AON.MSG.generatedDeduction(),
		AON.MSG.reducedDeduction(),
		AON.MSG.quotableAmount(),
		AON.MSG.pendingDueToQuota()};
	
	private static final String[] HEADERS_4 = new String[]{null,
			AON.MSG.deductionTaxablebase(),
			AON.MSG.liquiMsg2(),
			AON.MSG.liquiMsg31(),
			AON.MSG.pendingAmount()};

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
		for (final Mod2002015Key key : Mod2002015Constants.LIQUIDATION_III_KEYS_1) {
			if (mod200Object.isVisible(key)) {
				row = paintKey(table,key,row);
				if (key == Mod2002015Key.BN570) {
					row = paintKeyBreakdownLink(table,row,AON.MSG.doubleContributionNationalPrevious()
							,Mod2002015BN570Key.values(),HEADERS_1);
				} 
				if (key == Mod2002015Key.BN571) {
					row = paintKeyBreakdownLink(table,row,AON.MSG.doubleContributionNational2015()
							,Mod2002015BN571Key.values(),HEADERS_1);
				} 
				if (key == Mod2002015Key.BN572) {
					row = paintKeyBreakdownLink(table,row,AON.MSG.doubleContributionInternationalPrevious()
							,Mod2002015BN572Key.values(),HEADERS_1);
				} 
				if (key == Mod2002015Key.BN573) {
					row = paintKeyBreakdownLink(table,row,AON.MSG.doubleContributionInternational2015()
							,Mod2002015BN573Key.values(),HEADERS_2);
				}
			}
		}
		row = 0;
		for (final Mod2002015Key key : Mod2002015Constants.LIQUIDATION_III_KEYS_2) {
			row = paintKey(table1,key,row);
			if (mod200Object.isVisible(key)) {
				if (key == Mod2002015Key.BN585) {
					row = paintKeyBreakdownLink(table1,row,Mod2002015Key.BN585.getDescription()
							,Mod2002015BN585Key.values(),HEADERS_2);
				} 
				if (key == Mod2002015Key.BN584) {
					row = paintKeyBreakdownLink(table1,row,Mod2002015Key.BN584.getDescription()
							,Mod2002015BN584Key.values(),HEADERS_2);
				} 
				if (key == Mod2002015Key.BN588) {
					row = paintKeyBreakdownLink(table1,row,Mod2002015Key.BN588.getDescription()
							,Mod2002015BN588Key.values(),HEADERS_2);
				} 
				if (key == Mod2002015Key.BN082) {
					row = paintKeyBreakdownLink(table1,row,Mod2002015Key.BN082.getDescription()
							,Mod2002015BN082Key.values(),HEADERS_3);
				} 
				if (key == Mod2002015Key.BN565) {
					row = paintKeyBreakdownLink(table1,row,Mod2002015Key.BN082.getDescription()
							,Mod2002015BN565Key.values(),HEADERS_2);
				} 
				if (key == Mod2002015Key.BN590) {
					row = paintKeyBreakdownLink(table1,row,Mod2002015Key.BN590.getDescription()
							,Mod2002015BN590Key.values(),HEADERS_2);
				}
				if (key == Mod2002015Key.BN1040) {
					row = paintKeyBreakdownLink(table1,row,Mod2002015Key.BN1040.getDescription()
							,Mod2002015BN1040Key.values(),HEADERS_4);
				}
				if (key == Mod2002015Key.BN1041) {
					row = paintKeyBreakdownLink(table1,row,Mod2002015Key.BN1041.getDescription()
							,Mod2002015BN1041Key.values(),HEADERS_4);
				}
			}
		}
	}
	
}
