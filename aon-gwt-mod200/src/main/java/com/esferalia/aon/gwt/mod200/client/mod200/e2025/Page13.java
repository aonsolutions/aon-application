// LIQUIDACION (V): CUOTA A INGRESAR O DEVOLVER, PAGOS FRACCIONADOS, RESULTADO DE LA AUTOLIQUIDACION
package com.esferalia.aon.gwt.mod200.client.mod200.e2025;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2025.Model2002025.Model2002025PageCallback;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Key;
import com.google.gwt.user.client.ui.FlexTable;

public class Page13 extends PageAbs {

	private static final String[] HEADERS_1 = new String[] {
			"Efectuados a la entidad",
			"Imputados por AIEs y UTEs"
	};
	
	private static final String[] HEADERS_2 = new String[] {
			"Estado",
			"D.Forales/Navarra (totales)"
	};

	private static final String[] HEADERS_3 = new String[] {
			"Total (Estado+DF/N)",
			"Estado",
			"D.Forales/Navarra (totales)"
	};
	
	private static final String[] HEADERS_4 = new String[] {
			"Estado",
			""
	};
	
	public Page13( Model2002025PageCallback callback ) {
		super(callback);
	}
	
	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		paintTable(AON.MSG.yearQuota(), Mod2002025Constants.LIQUIDATION_V_KEYS_1, HEADERS_1, Mod2002025Key.LQ1766);
		paintTable("", Mod2002025Constants.LIQUIDATION_V_KEYS_2, HEADERS_2, Mod2002025Key.BN599);
		paintTable(AON.MSG.splittedPayments(), Mod2002025Constants.LIQUIDATION_V_KEYS_3, HEADERS_2, Mod2002025Key.BN611);
		paintTable("Resultado de la autoliquidaci\u00F3n", Mod2002025Constants.LIQUIDATION_V_KEYS_4, HEADERS_2, null);
		paintTable("", Mod2002025Constants.LIQUIDATION_V_KEYS_5, HEADERS_3, Mod2002025Key.LQ1586);
		paintFooterNote(basePanel, "(**) Incumplimiento de requisitos o tributaci\u00F3n por otro r\u00E9gimen antes del plazo de 3 a\u00F1os de permanencia (art. 9.1 Ley 11/2009)");
		paintTable("Rectificativa", Mod2002025Constants.LIQUIDATION_V_KEYS_6, HEADERS_2, Mod2002025Key.BN621);
		paintTable(null, new Mod2002025Key[][] {{Mod2002025Key.ING01,null}, {Mod2002025Key.ING02,null}}, HEADERS_2, null);
		paintTable("Rectificaci\u00F3n", Mod2002025Constants.LIQUIDATION_V_KEYS_6_1, HEADERS_4, Mod2002025Key.LQ866);
		paintTable("Opci\u00F3n de fraccionamiento en supuestos de cambios de residencia (art. 19.1 LIS)", Mod2002025Constants.LIQUIDATION_V_KEYS_7, HEADERS_2, Mod2002025Key.LQ2485);
		basePanel.add(getSubtitle("Rectificativa"));
		paintTable("", Mod2002025Constants.LIQUIDATION_V_KEYS_8, HEADERS_2, Mod2002025Key.LQ2489);
		paintTable("Conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS)", Mod2002025Constants.LIQUIDATION_V_KEYS_9, HEADERS_3, null);
		basePanel.add(getSubtitle("Rectificativa"));
		paintTable("", Mod2002025Constants.LIQUIDATION_V_KEYS_10, HEADERS_3, null);
		
	}
	
	private void paintTable(String title, Mod2002025Key[][] liquidationKeys, String[] headers, Mod2002025Key boldKey) {
		
		FlexTable table = addTable(title, headers.length);
	
		int row = 0;
		
		if (title != null) {
			paintEmptyCell(table, row, 0);
			int col = 1;
			for (String s : headers) {
				addHeaderCell(table, row, col, s, false);
				col++;			
			}
			row++;
		}
		
		for (Mod2002025Key[] keys : liquidationKeys) {
			boolean paintDescription = true;							
			for (int i = 0; i < keys.length; i++) {
				if (keys[i] != null) {
					if (paintDescription) {
						boolean bold = false;
						if (boldKey != null && keys[i] == boldKey)
							bold = true;
						paintDescription(table, keys[i].getDescription(), row, 0, bold, 0);
						paintDescription = false;
					}
					paintKeyField(table, keys[i], row, i+1);					
				}
			}
			row++;
		}
		
	}

}
