// LIQUIDACION (III): BONIFICACIONES Y DEDUCCIONES POR DOBLE IMPOSICION, CUOTA INTEGRA AJUSTADA
package com.esferalia.aon.gwt.fiscal.client.mod200.e2019;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2019.Model2002019.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200.IMod200Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN1280Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN1344Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN570Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN571Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN572Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019BN573Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019Key;
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
		"2019 deducci\u00F3n pendiente",/*********/
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
		for (final Mod2002019Key key : Mod2002019Constants.LIQUIDATION_III_KEYS) {
			if (callback.getMod200Object().isVisible(key)) {
				
				if (key == Mod2002019Key.BN570) {
					paintDescription(table, "Deducciones por doble imposici\u00F3n:", row, 0, false);
					row++;
					margin = true;
				}
				
				if (key == Mod2002019Key.BN581) {
					margin = false;
				}
				
				row = paintKey(table,key,row);
				
				if (margin) {
					table.getCellFormatter().addStyleName(row-1, 0, AON.AON_CSS.aonPaddingLeft());
				}				
				
				if (key == Mod2002019Key.BN570) {					
					row = paintKeyBreakdownLink(table,row,Mod2002019Key.BN570,Mod2002019BN570Key.values(),HEADERS_1);
				} 
				if (key == Mod2002019Key.BN1344) {
					row = paintKeyBreakdownLink(table,row,Mod2002019Key.BN1344,Mod2002019BN1344Key.values(),HEADERS_1);
				}
				if (key == Mod2002019Key.BN1280) {
					row = paintKeyBreakdownLink(table,row,Mod2002019Key.BN1280,Mod2002019BN1280Key.values(),HEADERS_2);
				}
				if (key == Mod2002019Key.BN572) {
					row = paintKeyBreakdownLink(table,row,Mod2002019Key.BN572,Mod2002019BN572Key.values(),HEADERS_1);
				} 
				if (key == Mod2002019Key.BN571) {
					row = paintKeyBreakdownLink(table,row,Mod2002019Key.BN571,Mod2002019BN571Key.values(),HEADERS_1);
				} 
				if (key == Mod2002019Key.BN573) {
					row = paintKeyBreakdownLink(table,row,Mod2002019Key.BN573,Mod2002019BN573Key.values(),HEADERS_2);
				}
				
				if (margin) {
					table.getCellFormatter().addStyleName(row-1, 0, AON.AON_CSS.aonPaddingLeft());
				}
			}
		}
	}
	
	@Override
	protected boolean isDisabled(IMod200Key key) {
		if (key == Mod2002019Key.BN575)  {
			return callback.getMod200Object().getMod200().isNotChecked(Mod2002019Key.C0007);
		}
		return super.isDisabled(key);
	}
	
	@Override
	protected void populate() {}
}
