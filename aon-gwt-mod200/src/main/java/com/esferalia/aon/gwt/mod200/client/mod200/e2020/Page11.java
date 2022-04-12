// LIQUIDACION (IV): OTRAS DEDUCCIONES, CUOTA LIQUIDA POSITIVA
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020BN082Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020BN1040Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020BN1041Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020BN565Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020BN584Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020BN585Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020BN588Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020BN590Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Key;
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
		AON.MSG.pendingDueToQuota(),
		"Deducci\u00F3n resto del grupo"};
	
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
		for (final Mod2002020Key key : Mod2002020Constants.LIQUIDATION_IV_KEYS) {
			row = paintKey(table,key,row);
			if (callback.getMod200Object().isVisible(key)) {
				if (key == Mod2002020Key.BN585) {
					row = paintKeyBreakdownLink(table,row,Mod2002020Key.BN585,Mod2002020BN585Key.values(),HEADERS_1);
				} 
				if (key == Mod2002020Key.BN584) {
					row = paintKeyBreakdownLink(table,row,Mod2002020Key.BN584,Mod2002020BN584Key.values(),HEADERS_1);
				} 
				if (key == Mod2002020Key.BN588) {
					row = paintKeyBreakdownLink(table,row,Mod2002020Key.BN588,Mod2002020BN588Key.values(),HEADERS_1);
				} 
				if (key == Mod2002020Key.BN082) {
					row = paintKeyBreakdownLink(table,row,Mod2002020Key.BN082,Mod2002020BN082Key.values(),HEADERS_2);
				} 
				if (key == Mod2002020Key.BN565) {
					row = paintKeyBreakdownLink(table,row,Mod2002020Key.BN565,Mod2002020BN565Key.values(),HEADERS_1);
				} 
				if (key == Mod2002020Key.BN590) {
					row = paintKeyBreakdownLink(table,row,Mod2002020Key.BN590,Mod2002020BN590Key.values(),HEADERS_1);
				}
				if (key == Mod2002020Key.BN1040) {
					row = paintKeyBreakdownLink(table,row,Mod2002020Key.BN1040,Mod2002020BN1040Key.values(),HEADERS_3);
				}
				if (key == Mod2002020Key.BN1041) {
					row = paintKeyBreakdownLink(table,row,Mod2002020Key.BN1041,Mod2002020BN1041Key.values(),HEADERS_3);
				}
				if (key == Mod2002020Key.BN1039) {
					FlexTable table2 = new FlexTable();
					table2.setWidth("100%");
					table2.setCellSpacing(0);
					table2.getColumnFormatter().setWidth(1, "150px");
					table2.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());					
					paintKey(table2,Mod2002020Key.BN1039M,0);
					table.setWidget(row, 0, table2);
					row++;
				}				
				if (key == Mod2002020Key.BN2314) {
					FlexTable table2 = new FlexTable();
					table2.setWidth("100%");
					table2.setCellSpacing(0);
				   	table2.getColumnFormatter().setWidth(1, "150px");
					table2.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());					
					paintKey(table2,Mod2002020Key.BN2314M,0);
					table.setWidget(row, 0, table2);
					row++;
				}
				
			}
		}
	}
	
	@Override
	protected void populate() {		
	}
	
}
