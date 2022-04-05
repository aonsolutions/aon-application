// LIMITACION EN LA DEDUCIBILIDAD DE GASTOS FINANCIEROS
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020LM1212Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020LM538Key;
import com.google.gwt.user.client.ui.FlexTable;

public class Page15 extends PageAbs {

	public Page15( Model200PageCallback callback ) {
		super(callback);
		addBasePanel();
		initializeTable();
	}

	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		// -----------------------------------------------------
		//  Limitación en la deducibilidad de gastos financieros.
		// -----------------------------------------------------
		
		FlexTable table = addTable(AON.MSG.deducibleLimitation(), 2);
		
		int row = 0;

		for (int i = 0; i < Mod2002020Constants.DEDUCIBLE_LIMITATION_KEYS_1.length; i++) {
			Mod2002020Key[] keys = Mod2002020Constants.DEDUCIBLE_LIMITATION_KEYS_1[i];
			if (keys == null) {
				if (i == 0) {
					paintDescription(table, AON.MSG.limitMsg1() , row, 0, true);
				} else {
					paintDescription(table, AON.MSG.limitMsg2() , row, 0, true);
				}
			} else {
				for (int x = 0; x < keys.length; x++) {
					Mod2002020Key key = keys[x]; 
					if (key != null && callback.getMod200Object().isVisible(key)) {
						paintDescription(table, key.getDescription(), row, 0, false, 95);
						if (key == Mod2002020Key.LM1250 || key == Mod2002020Key.LM1251
						 || key == Mod2002020Key.LM1252 || key == Mod2002020Key.LM1253
						 || key == Mod2002020Key.LM1254) {
							table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPadding2Left());					
						}
						paintKeyField(table,key,row,x+1,10);
					}
				}
			}
			++row;
		}

		// -----------------------------------------------------
		// Limitación en la deducibilidad de gastos financieros. 
		// Gastos financieros pendientes de deducir
		// -----------------------------------------------------
		
		FlexTable table1 = addTable(AON.MSG.deducibleLimitationPending(), 5, "180px");
		
		row = 0;
		addHeaderCell(table1,row, 1,AON.MSG.previousPending());  
		table1.getFlexCellFormatter().setColSpan(row, 1, 2);
		addHeaderCell(table1,row, 3,AON.MSG.liquiMsg4());
		table1.getFlexCellFormatter().setColSpan(row, 3, 2);
		++row;
		paintKeysProvider(Mod2002020LM1212Key.values(), table1, row, new String[] {
				AON.MSG.fiscalYear(),
				AON.MSG.liquiMsg21(),
				AON.MSG.remainder() ,
				AON.MSG.liquiMsg3() ,
				AON.MSG.liquiMsg21(),
				AON.MSG.remainder()
			});
		
		// ---------------------------------------------------------------		 
		// Pendiente de adición por límite beneficio operativo no aplicado
		// ---------------------------------------------------------------
		
		paintKeysProvider(Mod2002020LM538Key.values(), addTable(AON.MSG.pendingAddinngs(), 3), new String[] {
				AON.MSG.liquiMsg1(),
				AON.MSG.liquiMsg2(),
				AON.MSG.liquiMsg3(),
				AON.MSG.liquiMsg4()				
			});
		
	}
	
	@Override
	protected void populate() {}
	
	
	@Override
	protected boolean isAvailable() {
		boolean av = super.isAvailable()
  		  && callback.getMod200Object().getMod200().isNotChecked(Mod2002020Key.C0009) 
  		  &&  callback.getMod200Object().getMod200().isNotChecked(Mod2002020Key.C0010);
		return av;
	}
	
}
