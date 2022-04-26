// LIQUIDACION (IV): OTRAS DEDUCCIONES, CUOTA LIQUIDA POSITIVA
package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Model2002021.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN082Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN1040Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN1041Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN565Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN584Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN585Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN588Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021BN590Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Key;
import com.google.gwt.user.client.ui.FlexTable;

public class Page11 extends PageAbs {

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

	public Page11( Model200PageCallback callback ) {
		super(callback);
		addBasePanel();
		initializeTable();
	}
	
	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		basePanel.add(getTitle(AON.MSG.otherDeductions()));
		
		FlexTable table = addTable();
		
		int row = 0;
		for (final Mod2002021Key key : Mod2002021Constants.LIQUIDATION_IV_KEYS) {
			row = paintKey(table,key,row);
			if (callback.getMod200Object().isVisible(key)) {
				if (key == Mod2002021Key.BN585) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN585,Mod2002021BN585Key.values(),HEADERS_1);
				} 
				if (key == Mod2002021Key.BN584) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN584,Mod2002021BN584Key.values(),HEADERS_1);
				} 
				if (key == Mod2002021Key.BN588) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN588,Mod2002021BN588Key.values(),HEADERS_1);
				} 
				if (key == Mod2002021Key.BN082) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN082,Mod2002021BN082Key.values(),HEADERS_2);
				} 
				if (key == Mod2002021Key.BN565) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN565,Mod2002021BN565Key.values(),HEADERS_1);
				} 
				if (key == Mod2002021Key.BN590) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN590,Mod2002021BN590Key.values(),HEADERS_1);
				}
				if (key == Mod2002021Key.BN1040) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN1040,Mod2002021BN1040Key.values(),HEADERS_3);
				}
				if (key == Mod2002021Key.BN1041) {
					row = paintKeyBreakdownLink(table,row,Mod2002021Key.BN1041,Mod2002021BN1041Key.values(),HEADERS_3);
				}
				if (key == Mod2002021Key.BN1039) {
					FlexTable table2 = new FlexTable();
					table2.setWidth("100%");
					table2.setCellSpacing(0);
					table2.getColumnFormatter().setWidth(1, "150px");
					table2.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());					
					paintKey(table2,Mod2002021Key.BN1039M,0);
					table.setWidget(row, 0, table2);
					row++;
				}				
				if (key == Mod2002021Key.BN2314) {
					FlexTable table2 = new FlexTable();
					table2.setWidth("100%");
					table2.setCellSpacing(0);
				   	table2.getColumnFormatter().setWidth(1, "150px");
					table2.addStyleName(AON.AON_CSS.aonFiscalPaddingLeft());					
					paintKey(table2,Mod2002021Key.BN2314M,0);
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
