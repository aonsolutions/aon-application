// REGIMEN ESPECIAL DE LA RESERVA PARA INVERSIONES EN CANARIAS
package com.esferalia.aon.gwt.mod200.client.mod200.e2022;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2022.Model2002022.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022RIC_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022RIC_2Key;
import com.google.gwt.user.client.ui.FlexTable;

public class Page16 extends PageAbs {

	public Page16( Model200PageCallback callback ) {
		super(callback);
	}

	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		// RIC 
		
		FlexTable table1 = addTable(AON.MSG.canariasRegime(), 5, "150px");
		
		int row = 0;
		addHeaderCell(table1, row, 2, "Aplicado/materializado en esta liquidaci\u00F3n");
		table1.getFlexCellFormatter().setColSpan(row, 2, 3);
		row++;
		paintKeysProvider(Mod2002022RIC_1Key.values(), table1, row, false, 
				"",
				"Pendiente de materializar RIC a principio de per\u00EDodo", 
				"Inversiones previstas letras A y B, art. 27.4 Ley 19/1994",
				"Inversiones previstas letras B bis, C y D, art. 27.4 Ley 19/1994",
				"Inversiones anticipadas consideradas materializaci\u00F3n de la RIC en esta liquidaci\u00F3n",
				"Pendiente de materializar RIC al final de per\u00EDodo" );
		
		// Importe de la dotación RIC con cargo a beneficios de 2022 (Casilla 927)
		FlexTable table2 = new FlexTable();
		basePanel.add(table2);
		
		table2.setCellSpacing(20);
		table2.getColumnFormatter().setWidth(0, "400px");
		table2.getColumnFormatter().setWidth(1, "200px");
		row = 0;
		paintKey(table2, Mod2002022Key.RC927, row);
		
		// Inversiones anticipadas

		paintKeysProvider(Mod2002022RIC_2Key.values(), addTable("", 4, "150px"), 0, false, 
				"",
				"Pendiente de dotar RIC a principio de per\u00EDodo",
				"Inversiones previstas letras A y B, art. 27.4 Ley 19/1994",
				"Inversiones previstas letras B bis, C y D, art. 27.4 Ley 19/1994",
				"Pendiente de dotar RIC al final de per\u00EDodo");
		
	} 
	
	@Override
	protected boolean isAvailable() {
		return super.isAvailable()
  		  && (callback.getMod200Object().getMod200().isChecked(Mod2002022Key.C0029));
	}
	
}
