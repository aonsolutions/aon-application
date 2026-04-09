// DOTACIONES DETERIORO CREDITOS, REVERSION PERDIDAS POR DETERIORO, ACTIVOS POR IMPUESTOS DIFERIDOS, EXCESO CUOTA LIQUIDA POSITIVA
package com.esferalia.aon.gwt.mod200.client.mod200.e2025;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2025.Model2002025.Model2002025PageCallback;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Constants;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LM1494Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LM1535Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LM1561Key;
import com.esferalia.aon.occam.mod200.api.model.mod200_2025.Mod2002025LM1579Key;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;

public class Page18 extends PageAbs {
	
	private static final String FOOTER_1494_1 = "(*) S\u00F3lo debe cumplimentarse si la entidad tiene dotaciones pendientes de integrar en un per\u00EDodo impositivo anterior iniciado en 2025.";
	private static final String FOOTER_1494_2 = "(**) Los importes se consignar\u00E1n a nivel de base. Cooperativas: sus importes deben ir referidos a cuota.";
	private static final String FOOTER_1535 = "(*) Activos por impuesto diferido con derecho a conversi\u00F3n en cr\u00E9dito exigible (art. 130 LIS).";
	private static final String FOOTER_1579 = "(*) S\u00F3lo debe cumplimentarse si la entidad tiene dotaciones pendientes de integrar correspondientes a un per\u00EDodo impositivo anterior iniciado en 2025.";

	public Page18( Model2002025PageCallback callback ) {
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
		paintKeysProvider(Mod2002025LM1494Key.values(), table, row, false, 
				AON.MSG.liquiMsg1(),
				AON.MSG.dot11(),
				AON.MSG.dot12(),
				AON.MSG.dot22(),
				AON.MSG.dot23(),
				AON.MSG.dot11(),
				AON.MSG.dot12() );
		
		paintFooterNote(basePanel, FOOTER_1494_1, FOOTER_1494_2);
		
		// Reversión de las pérdidas por deterioro de valores representativos de la participación en el capital o en los fondos propios de entidades pendientes de reversión (DT 16ª LIS).

		FlexTable table1 = addTable("Reversi\u00F3n de las p\u00E9rdidas por deterioro de valores representativos de la participaci\u00F3n en el capital o en los fondos propios de entidades pendientes de reversi\u00F3n (DT 16\u00AA LIS)");		
		
		paintDescription(table1, "N\u00BA per\u00EDodo impositivo (*)", 0, 0, false);
		paintKeyField(table1,Mod2002025Key.RV000 , 0, 1,  5, false); // Número de período impositivo (*)
		paintDescription(table1, "Dotaciones pendientes de integraci\u00F3n a principio del per\u00EDodo", 1, 0, false);
		paintKeyField(table1,Mod2002025Key.RV941 , 1, 1, 10, false); // Dotaciones pendientes de integración a principio del período
		paintDescription(table1, "Dotaciones integradas en esta liquidaci\u00F3n: DT 16\u00AA.1 y 2 LIS", 2, 0, false);
		paintKeyField(table1,Mod2002025Key.RV2810, 2, 1, 10, false); // Dotaciones integradas en esta liquidación DT 16ª.1 y 2 LIS
		paintDescription(table1, "Dotaciones integradas en esta liquidaci\u00F3n: DT 16\u00AA.3 LIS", 3, 0, false);
		paintKeyField(table1,Mod2002025Key.RV990 , 3, 1, 10, false); // Dotaciones integradas en esta liquidación DT 16ª.3 LIS
		paintDescription(table1, "Dotaciones pendientes de integraci\u00F3n en per\u00EDodos futuros", 4, 0, false);
		paintKeyField(table1,Mod2002025Key.RV991 , 4, 1, 10, false); // Dotaciones pendientes de integración en períodos futuros
		
		paintFooterNote(basePanel, "(*) Se indicar\u00E1 cual es el n\u00FAmero de per\u00EDodo impositivo objeto de declaraci\u00F3n a contar a partir de 1 de enero de 2024, considerando incluidos los per\u00EDodos inferiores a 12 meses.");
				
		// Activos por impuesto diferido DT 33 ...
		
		basePanel.add(getTitle(AON.MSG.damageAmount4()));
		
		FlexTable table2 = addTable("", 8, "150px", true);
		paintAmountLabel(table2);
		
		row = 1;
		addHeaderCell(table2, row, 5, AON.MSG.dot4_2());
		table2.getFlexCellFormatter().setColSpan(row, 5, 4);
		row++;
		paintKeysProvider(Mod2002025LM1535Key.values(), table2, row, false, 
				AON.MSG.liquiMsg1(),  
				AON.MSG.dot30() + " (*)",
				AON.MSG.dot41(),
				AON.MSG.dot31(),
				AON.MSG.dot32(),
				AON.MSG.dot33(),
				AON.MSG.dot34(),
				AON.MSG.dot35(),
				AON.MSG.dot36() + " (*)" );
		
		paintFooterNote(basePanel, FOOTER_1535);
		
		// Activos por impuesto diferido Art.130 LIS
		
		basePanel.add(getTitle(AON.MSG.damageAmount5()));
		
		FlexTable table3 = addTable("", 10, "150px", true);
		paintAmountLabel(table3);
		
		row = 1;
		addHeaderCell(table3, row, 3, AON.MSG.dot4_1());		
		table3.getFlexCellFormatter().setColSpan(row, 3, 3);
		addHeaderCell(table3, row, 6, AON.MSG.dot4_2());
		table3.getFlexCellFormatter().setColSpan(row, 6, 3);
		row++;
		paintKeysProvider(Mod2002025LM1561Key.values(), table3, row, false, 
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
			);		
		
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
		for (int i = 0; i < Mod2002025Constants.DOTACION_KEYS_5.length; i++) {
			Mod2002025Key key = Mod2002025Constants.DOTACION_KEYS_5[i]; 
			if (key != null && callback.getMod200Object().isVisible(key)) {
				paintKeyField(table4, key, row, i+1, 10, false);
			}
		}
		
		// Exceso cuota líquida positiva ...
		
		basePanel.add(getTitle(AON.MSG.damageAmount6()));
		
		FlexTable table5 = addTable("", 4);
		paintAmountLabel(table5);
		
		paintKeysProvider(Mod2002025LM1579Key.values(), table5, 1, false, 
				AON.MSG.liquiMsg1(),
				AON.MSG.dot50(),
				AON.MSG.dot51(),
				AON.MSG.dot52(),
				AON.MSG.dot53() );		
		paintFooterNote(basePanel, FOOTER_1579);
						
	}
	
	private void paintAmountLabel(FlexTable table) {
		Label desc = new Label("Los importes de este apartado se consignar\u00E1n a nivel de cuota:");
		desc.setStyleName(AON.AON_CSS.aonBold());
		desc.addStyleName(AON.CSS.aonFontSmall());
		table.setWidget(0, 0, desc);
		table.getFlexCellFormatter().setColSpan(0, 0, 4);
	}

}