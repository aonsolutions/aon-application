// DOTACIONES DETERIORO CREDITOS, REVERSION PERDIDAS POR DETERIORO, 
// ACTIVOS POR IMPUESTOS DIFERIDOS, EXCESO CUOTA LIQUIDA POSITIVA
package com.esferalia.aon.gwt.mod200.client.mod200.e2021;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2021.Model2002021.Model200PageCallback;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LM1494Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LM1535Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LM1561Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2021.Mod2002021LM1579Key;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class Page17 extends PageAbs {
	
	private static final String FOOTER_1494_1 = "(*) S\u00F3lo debe cumplimentarse si la entidad tiene dotaciones pendientes de integrar en un per\u00EDodo impositivo anterior iniciado en 2021.";
	private static final String FOOTER_1494_2 = "(**) Los importes se consignar\u00E1n a nivel de base. Cooperativas: sus importes deben ir referidos a cuota.";
	private static final String FOOTER_1535 = "(*) Activos por impuesto diferido con derecho a conversi\u00F3n en cr\u00E9dito exigible (art. 130 LIS).";
	private static final String FOOTER_1579 = "(*) S\u00F3lo debe cumplimentarse si la entidad tiene dotaciones pendientes de integrar correspondientes a un per\u00EDodo impositivo anterior iniciado en 2021.";

	public Page17( Model200PageCallback callback ) {
		super(callback);
	}

	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		// Dotaciones por deterioro de créditos u otros activos ...
		
		FlexTable table = addTable(AON.MSG.damageAmount2(), 6, "150px");

		int row = 0;
		addHeaderCell(table, row, 1, AON.MSG.dot1());
		table.getFlexCellFormatter().setColSpan(row, 1, 2);
		addHeaderCell(table, row, 4, AON.MSG.dot2());
		table.getFlexCellFormatter().setColSpan(row, 4, 2);
		row++;
		paintKeysProvider(Mod2002021LM1494Key.values(), table, row, false, new String[] {
				AON.MSG.liquiMsg1(),
				AON.MSG.dot11(),
				AON.MSG.dot12(),
				AON.MSG.dot22(),
				AON.MSG.dot23(),
				AON.MSG.dot11(),
				AON.MSG.dot12()
			});
		
		paintFooterNote(basePanel, FOOTER_1494_1, FOOTER_1494_2);
				
		// Activos por impuesto diferido DT 33 ...
		
		basePanel.add(getTitle(AON.MSG.damageAmount4()));
		
		FlexTable table2 = addTable("", 8, "150px", true);
		paintAmountLabel(table2);
		
		row = 1;
		addHeaderCell(table2,row, 5,AON.MSG.dot4_2());
		table2.getFlexCellFormatter().setColSpan(row, 5, 4);
		row++;
		paintKeysProvider(Mod2002021LM1535Key.values(), table2, row, false, new String[] {
				AON.MSG.liquiMsg1(),  
				AON.MSG.dot30() + " (*)",
				AON.MSG.dot41(),
				AON.MSG.dot31(),
				AON.MSG.dot32(),
				AON.MSG.dot33(),
				AON.MSG.dot34(),
				AON.MSG.dot35(),
				AON.MSG.dot36() + " (*)"
			});
		
		paintFooterNote(basePanel, FOOTER_1535);
		
		// Activos por impuesto diferido Art.130 LIS
		
		basePanel.add(getTitle(AON.MSG.damageAmount5()));
		
		FlexTable table3 = addTable("", 10, "150px", true);
		paintAmountLabel(table3);
		
		row = 1;
		addHeaderCell(table3,row, 3,AON.MSG.dot4_1());		
		table3.getFlexCellFormatter().setColSpan(row, 3, 3);
		addHeaderCell(table3,row, 6,AON.MSG.dot4_2());
		table3.getFlexCellFormatter().setColSpan(row, 6, 3);
		row++;
		paintKeysProvider(Mod2002021LM1561Key.values(), table3, row, false, new String[] {
				AON.MSG.liquiMsg1(),
				AON.MSG.dot40(),
				AON.MSG.dot41(),
				AON.MSG.dot42(),
				AON.MSG.dot43(),
				AON.MSG.dot44(),
				AON.MSG.dot45(),
				AON.MSG.dot46(),
				AON.MSG.dot47(),
				AON.MSG.dot48(),
				AON.MSG.dot49()
			});
		
		// Conversión de activos por impuesto diferido ...
		
		basePanel.add(getTitle(AON.MSG.damageAmount1()));
		
		FlexTable table4 = addTable("", 3);
		paintAmountLabel(table4);
		
		row = 1;
		addHeaderCell(table4, row, 0, "");
		addHeaderCell(table4, row, 1, "");
		addHeaderCell(table4, row, 2, "Abono", false);
		addHeaderCell(table4, row, 3, "Compensaci\u00F3n", false);
		++row;
		paintDescription(table4, "Importe del cr\u00E9dito exigible" , row, 0, false);
		for (int i = 0; i < Mod2002021Constants.DOTACION_KEYS_5.length; i++) {
			Mod2002021Key key = Mod2002021Constants.DOTACION_KEYS_5[i]; 
			if (key != null && callback.getMod200Object().isVisible(key)) {
				paintKeyField(table4, key, row, i+1, 10, false);
			}
		}
		
		// Exceso cuota líquida positiva ...
		
		basePanel.add(getTitle(AON.MSG.damageAmount6()));
		
		FlexTable table5 = addTable("", 4);
		paintAmountLabel(table5);
		
		paintKeysProvider(Mod2002021LM1579Key.values(), table5, 1, false, new String[] {
				AON.MSG.liquiMsg1(),
				AON.MSG.dot50(),
				AON.MSG.dot51(),
				AON.MSG.dot52(),
				AON.MSG.dot53()
			});
	
		paintFooterNote(basePanel, FOOTER_1579);
						
	}
	
	private void paintAmountLabel(FlexTable table) {
		Label desc = new Label("Los importes de este apartado se consignar\u00E1n a nivel de cuota:");
		desc.setStyleName(AON.AON_CSS.aonBold());
		desc.addStyleName(AON.CSS.aonFontSmall());
		table.setWidget(0, 0, desc);
		table.getFlexCellFormatter().setColSpan(0, 0, 4);
	}

//	@Override
//	protected void populate() {}
	
}
