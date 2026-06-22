// LIQUIDACION (III): BONIFICACIONES Y DEDUCCIONES POR DOBLE IMPOSICION, CUOTA INTEGRA AJUSTADA POSITIVA
package com.esferalia.aon.gwt.mod200.client.mod200.e2025;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2025.Model2002025.Model2002025PageCallback;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN1280Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN1344Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN570Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN571Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN572Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025BN573Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Key;
import com.google.gwt.user.client.ui.FlexTable;

public class Page11 extends PageAbs {

	private static final String[] HEADERS_1 = new String[] {
			null,
			"Deducci\u00F3n pendiente",
			"Tipo gravamen per\u00EDodo generaci\u00F3n",
			"2025 deducci\u00F3n pendiente",
			AON.MSG.current(),
			AON.MSG.futurePending()
	};
		
	private static final String[] HEADERS_2 = new String[] {
			null,
			AON.MSG.generatedDeduction(),
			AON.MSG.current(),
			AON.MSG.futurePending()
	};
	
	private static final String FOOTER_1 = "(*) S\u00F3lo debe cumplimentarse esta fila si la entidad tiene deducciones pendientes de aplicar correspondientes a un per\u00EDodo impositivo anterior iniciado en 2025.";
	
	public Page11( Model2002025PageCallback callback ) {
		super(callback);
	}
	
	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		basePanel.add(getTitle(AON.MSG.bonus()));
		
		FlexTable table = addTable();
		
		int row = 0;
		boolean margin = false;
		for (final Mod2002025Key key : Mod2002025Constants.LIQUIDATION_III_KEYS) {
			if (callback.getMod200Object().isVisible(key)) {
				
				if (key == Mod2002025Key.BN570) {
					paintDescription(table, "Deducciones por doble imposici\u00F3n:", row, 0, false);
					row++;
					margin = true;
				}
				
				if (key == Mod2002025Key.BN581) {
					margin = false;
				}
				
				row = paintKey(table,key,row);
				
				if (margin) {
					table.getCellFormatter().addStyleName(row-1, 0, AON.AON_CSS.aonPaddingLeft());
				}
				
				// Casillas con Desgloses
				
				if (key == Mod2002025Key.BN570) {					
					row = paintKeyBreakdownLink(table, row, Mod2002025Key.BN570, Mod2002025BN570Key.values(), HEADERS_1);
				} 
				if (key == Mod2002025Key.BN1344) {
					row = paintKeyBreakdownLink(table, row, Mod2002025Key.BN1344, Mod2002025BN1344Key.values(), HEADERS_1, FOOTER_1);
				}
				if (key == Mod2002025Key.BN1280) {
					row = paintKeyBreakdownLink(table, row, Mod2002025Key.BN1280, Mod2002025BN1280Key.values(), HEADERS_2);
				}
				if (key == Mod2002025Key.BN572) {
					row = paintKeyBreakdownLink(table, row, Mod2002025Key.BN572, Mod2002025BN572Key.values(), HEADERS_1);
				} 
				if (key == Mod2002025Key.BN571) {
					row = paintKeyBreakdownLink(table, row, Mod2002025Key.BN571, Mod2002025BN571Key.values(), HEADERS_1, FOOTER_1);
				} 
				if (key == Mod2002025Key.BN573) {
					row = paintKeyBreakdownLink(table, row, Mod2002025Key.BN573, Mod2002025BN573Key.values(), HEADERS_2);
				}
				
				if (margin) {
					table.getCellFormatter().addStyleName(row-1, 0, AON.AON_CSS.aonPaddingLeft());
				}
			}
		}
	}
	
	@Override
	protected boolean isDisabled(IMod200Key key) {
		if (key == Mod2002025Key.BN575)  {
			return callback.getMod200Object().getMod200().isNotChecked(Mod2002025Key.C0007);
		}
		return super.isDisabled(key);
	}

}
