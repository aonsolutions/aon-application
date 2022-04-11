// REGIMEN ESPECIAL CANARIAS
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.IMod200Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020RIC_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020RIC_2Key;
import com.google.gwt.user.client.ui.FlexTable;

public class Page16 extends PageAbs {

	public Page16( Model200PageCallback callback ) {
		super(callback);
		addBasePanel();
		initializeTable();
	}

	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		// RIC
		
		FlexTable table1 = addTable(AON.MSG.canariasRegime(), 5, "180px");
		
		int row = 0;
		addHeaderCell(table1,row, 2,"Aplicado/materializado en esta liquidaci\u00F3n");
		table1.getFlexCellFormatter().setColSpan(row, 2, 3);
		row++;
		paintKeysProvider(Mod2002020RIC_1Key.values(), table1, row, new String[] {
				"",
				"Pendiente de materializar RIC a principio de per\u00EDodo", 
				"Inversiones previstas letras A y B, art. 27.4 Ley 19/1994",
				"Inversiones previstas letras B bis, C y D, art. 27.4 Ley 19/1994",
				"Inversiones anticipadas consideradas materializaci\u00F3n de la RIC en esta liquidaci\u00F3n",
				"Pendiente de materializar RIC al final de per\u00EDodo"	
			});
		
		// Importe de la dotación RIC con cargo a beneficios de 2020 (Casilla 927)
		FlexTable table2 = new FlexTable();
		basePanel.add(table2);
		
		table2.setCellSpacing(20);
		table2.getColumnFormatter().setWidth(0, "400px");
		table2.getColumnFormatter().setWidth(1, "200px");
		row = 0;
		paintKey(table2,Mod2002020Key.RC927,row);
		
		// Inversiones anticipadas

		paintKeysProvider(Mod2002020RIC_2Key.values(), addTable(4), new String[] {
				"",
				"Pendiente de dotar RIC a principio de per\u00EDodo",
				"Inversiones previstas letras A y B, art. 27.4 Ley 19/1994",
				"Inversiones previstas letras B bis, C y D, art. 27.4 Ley 19/1994",
				"Pendiente de dotar RIC al final de per\u00EDodo"
			});
		
	}
	
	@Override
	protected void populate() {
	}
	
	@Override
	protected boolean isAvailable() {
		boolean av = super.isAvailable()
  		  && (callback.getMod200Object().getMod200().isChecked(Mod2002020Key.C0029));
		return av;
	}
	
	@Override
	protected boolean isDisabled(IMod200Key key) {
		return super.isDisabled(key);
	}
	
}
