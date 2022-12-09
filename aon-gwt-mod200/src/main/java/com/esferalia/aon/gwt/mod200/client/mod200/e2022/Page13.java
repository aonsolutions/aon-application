// TRIBUTACION CONJUNTA
package com.esferalia.aon.gwt.mod200.client.mod200.e2022;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2022.Model2002022.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key;
import com.google.gwt.user.client.ui.FlexTable;

public class Page13 extends PageAbs {
	
	private static final String[] HEADERS = new String[] {
			"ARABA",
			"GIPUZKOA",
			"BIZKAIA",
			"NAVARRA",
			"TOTAL"
	};
	
	public Page13( Model200PageCallback callback ) {
		super(callback);
	}
	
	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		addTable(AON.MSG.combinedTaxation1(), Mod2002022Constants.COMBINED_TAXATION_1);
		addTable(AON.MSG.combinedTaxation2(), Mod2002022Constants.COMBINED_TAXATION_2);
		
		paintTable("Determinaci\u00F3n del l\u00EDquido a ingresar o a devolver a cada una de las Administraciones", Mod2002022Constants.COMBINED_TAXATION_3, HEADERS, Mod2002022Key.TR420, Mod2002022Key.TR474, Mod2002022Key.TR494, Mod2002022Key.TR1624);
		paintTable("Opci\u00F3n de fraccionamiento art. 19.1 LIS", Mod2002022Constants.COMBINED_TAXATION_4, HEADERS, Mod2002022Key.TR1646, Mod2002022Key.TR1654);
		paintTable("Conversi\u00F3n de activos por impuesto diferido en cr\u00E9dito exigible frente a la Administraci\u00F3n tributaria (art. 130 LIS)", Mod2002022Constants.COMBINED_TAXATION_5, HEADERS);
		
	}
	
	private void paintTable(String title, Mod2002022Key[][] liquidationKeys, String[] headers, Mod2002022Key... boldKeys) {
		
		FlexTable table = addTable(title, headers.length, "140px");
	
		int row = 0;
		
		paintEmptyCell(table, row, 0);
		int col = 1;
		for (String s : headers) {
			addHeaderCell(table, row, col, s, false);
			col++;			
		}
		
		row++;
		
		for (Mod2002022Key[] keys : liquidationKeys) {
			boolean paintDescription = true;							
			for (int i = 0; i < keys.length; i++) {
				if (keys[i] != null) {
					if (paintDescription) {
						boolean bold = false;
						for (Mod2002022Key key : boldKeys) {
							if (keys[i] == key)
								bold = true;
						}						
						paintDescription(table, keys[i].getDescription(), row, 0, bold, 0);
						paintDescription = false;
					}
					paintKeyField(table,keys[i], row, i+1, 8, false);					
				}
			}
			row++;
		}
		
	}
	
	@Override
	protected boolean isAvailable() {
		boolean av = super.isAvailable()
  		  && (callback.getMod200Object().getMod200().isChecked(Mod2002022Key.C0028));
		return av;
	}
	
}
