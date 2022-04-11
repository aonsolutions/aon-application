// LIQUIDACION (V): CUOTA A INGRESAR O DEVOLVER, PAGOS FRACCIONADOS, LIQUIDO A INGRESAR O DEVOLVER
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020Key;
import com.google.gwt.user.client.ui.FlexTable;

public class Page12 extends PageAbs {

	private static final String[] HEADERS_1 = new String[]{
			"Efectuados a la entidad",
			"Imputados por AIEs y UTEs"};
	
	private static final String[] HEADERS_2 = new String[]{
			"Estado",
			"D.Forales/Navarra (totales)"};

	private static final String[] HEADERS_3 = new String[]{
			"Total (Estado+DF/N)",
			"Estado",
			"D.Forales/Navarra (totales)"};
	
	public Page12( Model200PageCallback callback ) {
		super(callback);
		addBasePanel();
		initializeTable();
	}
	
	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		paintTable(addTable(AON.MSG.yearQuota(), 2), Mod2002020Constants.LIQUIDATION_V_KEYS_1, HEADERS_1);
		paintTable(addTable(2), Mod2002020Constants.LIQUIDATION_V_KEYS_2, HEADERS_2);
		
		paintTable(addTable(AON.MSG.splittedPayments(), 2), Mod2002020Constants.LIQUIDATION_V_KEYS_3, HEADERS_2);
		
		paintTable(addTable(AON.MSG.netQuota(),2), Mod2002020Constants.LIQUIDATION_V_KEYS_4, HEADERS_2);
		paintTable(addTable(3), Mod2002020Constants.LIQUIDATION_V_KEYS_5, HEADERS_3);		
		
	}
	
	private void paintTable(FlexTable table, Mod2002020Key[][] liquidationKeys, String... headers) {
	
		int row = 0;
		
		paintEmptyCell(table, row, 0);
		int col = 1;
		for (String s : headers) {
			paintTitle(table, s, row, col);
			col++;			
		}
		
		row++;
		
		for (Mod2002020Key[] keys : liquidationKeys) {
			boolean paintDescription = true;							
			for (int i = 0; i < keys.length; i++) {
				if (keys[i] != null) {
					if (paintDescription) {
						paintKeyDescription(table, keys[i], row, 0);
						paintDescription = false;
						// Casilla 621, la descripcion va en negrita
						if (keys[i] == Mod2002020Key.BN621) {
							table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
						}
					}
					paintKeyField(table,keys[i], row, i+1);
				}
			}
			row++;
		}
		
	}

	@Override
	protected void populate() {}
	
}



