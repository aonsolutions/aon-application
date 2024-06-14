// RIC, RIIB
package com.esferalia.aon.gwt.mod200.client.mod200.e2023;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2023.Model2002023.Model2002023PageCallback;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023RIC_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023RIC_2Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023RIIB_1Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2023.Mod2002023RIIB_2Key;
import com.google.gwt.user.client.ui.FlexTable;

public class Page17 extends PageAbs {

	public Page17( Model2002023PageCallback callback ) {
		super(callback);
	}

	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		// Régimen especial de la reserva para inversiones en Canarias
		if (callback.getMod200Object().getMod200().isChecked(Mod2002023Key.C0029)) {
			
			// RIC
		
			FlexTable table1 = addTable(AON.MSG.canariasRegime(), 6, "150px");
			
			int row = 0;
			addHeaderCell(table1, row, 2, "Aplicado/materializado en esta liquidaci\u00F3n");
			table1.getFlexCellFormatter().setColSpan(row, 2, 4);
			row++;
			paintKeysProvider(Mod2002023RIC_1Key.values(), table1, row, false, 
					"",
					"Pendiente de materializar RIC a principio de per\u00EDodo", 
					"Inversiones previstas letras A y B, art. 27.4 Ley 19/1994",
					"Inversiones previstas letras B bis, C y D, art. 27.4 Ley 19/1994",
					"Inversiones anticipadas consideradas materializaci\u00F3n de la RIC en esta liquidaci\u00F3n",
					"Integrado en BI por incumplimiento de requisitos",
					"Pendiente de materializar RIC al final de per\u00EDodo" );
			
			// Importe de la dotación RIC con cargo a beneficios de 2023 (Casilla 927)
			
			FlexTable table2 = new FlexTable();
			basePanel.add(table2);
			
			table2.setCellSpacing(20);
			table2.getColumnFormatter().setWidth(0, "400px");
			table2.getColumnFormatter().setWidth(1, "200px");
			row = 0;
			paintKey(table2, Mod2002023Key.RC927, row);
			
			// RIC - Inversiones anticipadas
	
			paintKeysProvider(Mod2002023RIC_2Key.values(), addTable("", 4, "150px"), 0, false, 
					"",
					"Pendiente de dotar RIC a principio de per\u00EDodo",
					"Inversiones previstas letras A y B, art. 27.4 Ley 19/1994",
					"Inversiones previstas letras B bis, C y D, art. 27.4 Ley 19/1994",
					"Pendiente de dotar RIC al final de per\u00EDodo");
		}
		
		// Régimen especial de la reserva para inversiones en las Illes Balears 
		if (callback.getMod200Object().getMod200().isChecked(Mod2002023Key.C0086)) {
		
			// RIIB 
			
			FlexTable table1 = addTable("R\u00E9gimen especial de la reserva para inversiones en las Illes Balears (DA 70 Ley 31/2022)", 6, "150px");
			
			int row = 0;
			addHeaderCell(table1, row, 2, "Aplicado/materializado en esta liquidaci\u00F3n");
			table1.getFlexCellFormatter().setColSpan(row, 2, 4);
			row++;
			paintKeysProvider(Mod2002023RIIB_1Key.values(), table1, row, false, 
					"",
					"Pendiente de materializar RIIB a principio de per\u00EDodo", 
					"Inversiones previstas letras A y B, DA 70.4 Ley 31/2022",
					"Inversiones previstas letra C, DA 70.4 Ley 31/2022",
					"Inversiones anticipadas consideradas materializaci\u00F3n de la RIIB en esta liquidaci\u00F3n",
					"Integrado en BI por incumplimiento de requisitos",
					"Pendiente de materializar RIIB al final de per\u00EDodo" );
			
			// Importe de la dotación RIIB con cargo a beneficios de 2023 (Casilla 02918)
			
			FlexTable table2 = new FlexTable();
			basePanel.add(table2);
			
			table2.setCellSpacing(20);
			table2.getColumnFormatter().setWidth(0, "400px");
			table2.getColumnFormatter().setWidth(1, "200px");
			row = 0;
			paintKey(table2, Mod2002023Key.RB2918, row);
			
			// RIIB - Inversiones anticipadas
	
			paintKeysProvider(Mod2002023RIIB_2Key.values(), addTable("", 4, "150px"), 0, false, 
					"",
					"Pendiente de dotar RIIB a principio de per\u00EDodo",
					"Inversiones previstas letras A y B, DA 70.4 Ley 31/2022",
					"Inversiones previstas letra C, DA 70.4 Ley 31/2022",
					"Pendiente de dotar RIIB al final de per\u00EDodo");
		
		}
		
	} 
	
	@Override
	protected boolean isAvailable() {		
		return super.isAvailable() && (isCheckedOr(Mod2002023Key.C0029,Mod2002023Key.C0086));
	}
	
}
