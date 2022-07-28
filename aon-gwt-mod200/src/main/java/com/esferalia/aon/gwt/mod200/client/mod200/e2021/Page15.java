// LIMITACION EN LA DEDUCIBILIDAD DE GASTOS FINANCIEROS
package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Model2002021.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LM1212Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LM538Key;
import com.google.gwt.user.client.ui.FlexTable;

public class Page15 extends PageAbs {
	
	private static final String FOOTER_1212_1 = "(*) S\u00F3lo debe cumplimentarse si la entidad tiene gastos financieros pendientes por otro per\u00EDodo impositivo iniciado en 2021, pero inferior a 12 meses y previo al declarado.";
	private static final String FOOTER_1212_2 = "(**) S\u00F3lo debe cumplimentarse si la entidad tiene gastos financieros pendientes, devengados en el propio per\u00EDodo impositivo, deducibles en los pr\u00F3ximos per\u00EDodos impositivos.";
	private static final String FOOTER_538_1 = "(*) S\u00F3lo debe cumplimentarse si la entidad tiene pendiente de adici\u00F3n por l\u00EDmite beneficio operativo no aplicado por otro per\u00EDodo impositivo iniciado en 2021, pero inferior a 12 meses y previo al declarado.";
	private static final String FOOTER_538_2 = "(**) S\u00F3lo debe cumplimentarse si la entidad tiene pendiente de adici\u00F3n por l\u00EDmite beneficio operativo no aplicado, generado en el propio per\u00EDodo impositivo, aplicable en los pr\u00F3ximos per\u00EDodos impositivos.";	

	public Page15( Model200PageCallback callback ) {
		super(callback);
	}

	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		// Limitación en la deducibilidad de gastos financieros. Art. 16 LIS (excluidos aquellos a que se refieren los arts. 15 g), h) y 15 bis LIS)
		
		FlexTable table = addTable("Limitaci\u00F3n en la deducibilidad de gastos financieros. Art. 16 LIS (excluidos aquellos a que se refieren los arts. 15 g), h) y 15 bis LIS)", 2, "150px");
		
		int row = 0;

		for (int i = 0; i < Mod2002021Constants.DEDUCIBLE_LIMITATION_KEYS_1.length; i++) {
			Mod2002021Key[] keys = Mod2002021Constants.DEDUCIBLE_LIMITATION_KEYS_1[i];
			if (keys == null) {
				if (i == 0) {
					paintDescription(table, "L\u00EDmite art. 16.5 y/o 83 LIS" , row, 0, true); // Límite art. 16.5 y/o 83 LIS
				} else {
					paintDescription(table, AON.MSG.limitMsg2() , row, 0, true); // Límite art. 16.1 y 16.2 LIS
				}
			} else {
				for (int x = 0; x < keys.length; x++) {
					Mod2002021Key key = keys[x]; 
					if (key != null && callback.getMod200Object().isVisible(key)) {						
						paintDescription(table, key.getDescription(), row, 0, key == Mod2002021Key.LM1260);
						if (key == Mod2002021Key.LM1250 || key == Mod2002021Key.LM1251
						 || key == Mod2002021Key.LM1252 || key == Mod2002021Key.LM1253
						 || key == Mod2002021Key.LM1254) {
							table.getCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPadding2Left());					
						}
						paintKeyField(table, key, row, x+1, 10, false);
					}
				}
			}
			++row;
		}

		// Limitación en la deducibilidad de gastos financieros. Gastos financieros pendientes de deducir
		
		FlexTable table1 = addTable(AON.MSG.deducibleLimitationPending(), 5, "150px");
		
		row = 0;
		addHeaderCell(table1,row, 1,AON.MSG.previousPending());  
		table1.getFlexCellFormatter().setColSpan(row, 1, 2);
		addHeaderCell(table1,row, 3,AON.MSG.liquiMsg4());
		table1.getFlexCellFormatter().setColSpan(row, 3, 2);
		++row;
		paintKeysProvider(Mod2002021LM1212Key.values(), table1, row, false, new String[] {
				AON.MSG.liquiMsg1(),
				AON.MSG.liquiMsg21(),
				AON.MSG.remainder() ,
				AON.MSG.liquiMsg3() ,
				AON.MSG.liquiMsg21(),
				AON.MSG.remainder()
			});
		
		paintFooterNote(basePanel, FOOTER_1212_1, FOOTER_1212_2);
		
		// Pendiente de adición por límite beneficio operativo no aplicado
		
		paintKeysProvider(Mod2002021LM538Key.values(), addTable(AON.MSG.pendingAddinngs(), 3), new String[] {
				AON.MSG.liquiMsg1(),
				"Importe generado. Pendiente de aplicaci\u00F3n a principio del periodo",
				AON.MSG.liquiMsg3(),
				AON.MSG.liquiMsg4()				
			});
		
		paintFooterNote(basePanel, FOOTER_538_1, FOOTER_538_2);
		
	}
	
	@Override
	protected boolean isAvailable() {
		boolean av = super.isAvailable()
  		  && callback.getMod200Object().getMod200().isNotChecked(Mod2002021Key.C0009) 
  		  &&  callback.getMod200Object().getMod200().isNotChecked(Mod2002021Key.C0010);
		return av;
	}
	
}
