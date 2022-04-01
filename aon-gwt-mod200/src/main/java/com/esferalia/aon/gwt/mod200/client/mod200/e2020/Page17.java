// DOTACIONES DETERIORO CREDITOS, REVERSION PERDIDAS POR DETERIORO, 
// ACTIVOS POR IMPUESTOS DIFERIDOS, EXCESO CUOTA LIQUIDA POSITIVA
package com.esferalia.aon.gwt.mod200.client.mod200.e2020;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.mod200.client.mod200.e2020.Model2002020.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020LM1494Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020LM1535Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020LM1561Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2020.Mod2002020LM1579Key;
import com.google.gwt.user.client.ui.FlexTable;

public class Page17 extends PageAbs {

//	interface PageBinder extends UiBinder<Widget, Page17> {}

//	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

//	@UiField
//	FlexTable table;
//	@UiField
//	FlexTable table1;
//	@UiField
//	FlexTable table2;
//	@UiField
//	FlexTable table3;
//	@UiField
//	FlexTable table4;
//	@UiField
//	FlexTable table5;
	
	public Page17( Model200PageCallback callback ) {
		super(callback);
//		table = new FlexTable();
//		Widget ui = pageBinder.createAndBindUi(this);
//		initWidget(ui);
		addBasePanel();		
		initializeTable();		
	}

	@Override
	protected void initializeTable() {
		
		basePanel.clear();
		
		// Dotaciones por deterioro de créditos ...
//		table.setWidth("100%");
//		table.addStyleName(AON.AON_CSS.aonMarginBottom());
//		table.setCellSpacing(0);
//		table.getColumnFormatter().setWidth(0, "auto");
//		table.getColumnFormatter().setWidth(1, "200px");
//		table.getColumnFormatter().setWidth(2, "200px");
//		table.getColumnFormatter().setWidth(3, "200px");
//		table.getColumnFormatter().setWidth(4, "200px");
//		table.getColumnFormatter().setWidth(5, "200px");
//		table.getColumnFormatter().setWidth(6, "200px");
		
		//{aonResources.css.aonFiscalScrollTableWrapper}
		FlexTable table = addTable(AON.MSG.damageAmount2(), 6, "150px", true);
//		table.addStyleName(AON.AON_CSS.aonMarginBottom());
//		table.addStyleName(AON.AON_CSS.aonFiscalScrollTableWrapper());

		int row = 0;
		addHeaderCell(table,row, 1,AON.MSG.dot1());
		table.getFlexCellFormatter().setColSpan(row, 1, 2);
		addHeaderCell(table,row, 4,AON.MSG.dot2());
		table.getFlexCellFormatter().setColSpan(row, 4, 2);
		row++;
//		addHeaderCell(table,row, 0,AON.MSG.liquiMsg1());
//		addHeaderCell(table,row, 1,AON.MSG.dot11());
//		addHeaderCell(table,row, 2,AON.MSG.dot12());
//		addHeaderCell(table,row, 3,AON.MSG.dot22());
//		addHeaderCell(table,row, 4,AON.MSG.dot23());
//		addHeaderCell(table,row, 5,AON.MSG.dot11());
//		addHeaderCell(table,row, 6,AON.MSG.dot12());
//		++row;
		paintKeysProvider(Mod2002020LM1494Key.values(), table, row, new String[] {
				AON.MSG.liquiMsg1(),
				AON.MSG.dot11(),
				AON.MSG.dot12(),
				AON.MSG.dot22(),
				AON.MSG.dot23(),
				AON.MSG.dot11(),
				AON.MSG.dot12()
			});
				
		// Reversión de las pérdidas ...
//		table1.setWidth("100%");
//		table1.addStyleName(AON.AON_CSS.aonMarginBottom());
//		table1.setCellSpacing(0);
//		table1.getColumnFormatter().setWidth(0, "auto");
//		table1.getColumnFormatter().setWidth(1, "200px");
//		table1.getColumnFormatter().setWidth(2, "200px");
//		table1.getColumnFormatter().setWidth(3, "200px");
//		table1.getColumnFormatter().setWidth(4, "200px");
		
		FlexTable table1 = addTable(AON.MSG.damageAmount3(), 4);
		
		row = 0;
		addHeaderCell(table1,row, 2,AON.MSG.dot22());
		table1.getFlexCellFormatter().setColSpan(row, 2, 2);
		row++;
		addHeaderCell(table1,row, 0,AON.MSG.numPer());
		addHeaderCell(table1,row, 1,AON.MSG.dot3());
		addHeaderCell(table1,row, 2,"DT 16.1 y 2 LIS");
		addHeaderCell(table1,row, 3,"DT 16.3 LIS");
		addHeaderCell(table1,row, 4,AON.MSG.dot2());
		++row;
		for (int i = 0; i < Mod2002020Constants.DOTACION_KEYS_2.length; i++) {
			Mod2002020Key key = Mod2002020Constants.DOTACION_KEYS_2[i]; 
			if (key != null && callback.getMod200Object().isVisible(key)) {
				paintKeyField(table1,key,row,i,10);
			}
		}
		
		// Activos por impuesto diferido DT 33a ... 
//		table2.setWidth("100%");
//		table2.addStyleName(AON.AON_CSS.aonMarginBottom());
//		table2.setCellSpacing(0);
//		table2.getColumnFormatter().setWidth(0, "auto");
//		table2.getColumnFormatter().setWidth(1, "200px");
//		table2.getColumnFormatter().setWidth(2, "200px");
//		table2.getColumnFormatter().setWidth(3, "200px");
//		table2.getColumnFormatter().setWidth(4, "200px");
//		table2.getColumnFormatter().setWidth(5, "200px");
//		table2.getColumnFormatter().setWidth(6, "200px");
//		table2.getColumnFormatter().setWidth(7, "200px");
//		table2.getColumnFormatter().setWidth(8, "200px");
		
		FlexTable table2 = addTable(AON.MSG.damageAmount4(), 8, "150px", true);
		row = 0;
		addHeaderCell(table2,row, 5,AON.MSG.dot4_2());
		table2.getFlexCellFormatter().setColSpan(row, 5, 4);
		row++;
//		addHeaderCell(table2,row, 0,"");
//		addHeaderCell(table2,row, 1,AON.MSG.dot30());
//		addHeaderCell(table2,row, 2,AON.MSG.dot41());
//		addHeaderCell(table2,row, 3,AON.MSG.dot31());
//		addHeaderCell(table2,row, 4,AON.MSG.dot32());
//		addHeaderCell(table2,row, 5,AON.MSG.dot33());
//		addHeaderCell(table2,row, 6,AON.MSG.dot34());
//		addHeaderCell(table2,row, 7,AON.MSG.dot35());
//		addHeaderCell(table2,row, 8,AON.MSG.dot36());
//		++row;
		paintKeysProvider(Mod2002020LM1535Key.values(), table2, row, new String[] {
				"",  
				AON.MSG.dot30(),
				AON.MSG.dot41(),
				AON.MSG.dot31(),
				AON.MSG.dot32(),
				AON.MSG.dot33(),
				AON.MSG.dot34(),
				AON.MSG.dot35(),
				AON.MSG.dot36()
			});
		
		// Activos por impuesto diferido Art.130 LIS
//		table3.setWidth("100%");
//		table3.addStyleName(AON.AON_CSS.aonMarginBottom());
//		table3.setCellSpacing(0);
//		table3.getColumnFormatter().setWidth( 0, "auto");
//		table3.getColumnFormatter().setWidth( 1, "200px");
//		table3.getColumnFormatter().setWidth( 2, "200px");
//		table3.getColumnFormatter().setWidth( 3, "200px");
//		table3.getColumnFormatter().setWidth( 4, "200px");
//		table3.getColumnFormatter().setWidth( 5, "200px");
//		table3.getColumnFormatter().setWidth( 6, "200px");
//		table3.getColumnFormatter().setWidth( 7, "200px");
//		table3.getColumnFormatter().setWidth( 8, "200px");
//		table3.getColumnFormatter().setWidth( 9, "200px");
//		table3.getColumnFormatter().setWidth(10, "200px");
		
		FlexTable table3 = addTable(AON.MSG.damageAmount5(), 10, "150px", true);
		
		row = 0;
		addHeaderCell(table3,row, 3,AON.MSG.dot4_1());		
		table3.getFlexCellFormatter().setColSpan(row, 3, 3);
		addHeaderCell(table3,row, 6,AON.MSG.dot4_2());
		table3.getFlexCellFormatter().setColSpan(row, 6, 3);
		row++;
//		addHeaderCell(table3,row, 0,"");
//		addHeaderCell(table3,row, 1,AON.MSG.dot40());
//		addHeaderCell(table3,row, 2,AON.MSG.dot41());
//		addHeaderCell(table3,row, 3,AON.MSG.dot42());
//		addHeaderCell(table3,row, 4,AON.MSG.dot43());
//		addHeaderCell(table3,row, 5,AON.MSG.dot44());
//		addHeaderCell(table3,row, 6,AON.MSG.dot45());
//		addHeaderCell(table3,row, 7,AON.MSG.dot46());
//		addHeaderCell(table3,row, 8,AON.MSG.dot47());
//		addHeaderCell(table3,row, 9,AON.MSG.dot48());
//		addHeaderCell(table3,row,10,AON.MSG.dot49());
//		++row;
		paintKeysProvider(Mod2002020LM1561Key.values(), table3, row, new String[] {
				"",
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
//		table4.setWidth("100%");
//		table4.addStyleName(AON.AON_CSS.aonMarginBottom());
//		table4.setCellSpacing(0);
//		table4.getColumnFormatter().setWidth( 0, "auto");
//		table4.getColumnFormatter().setWidth( 1, "200px");
//		table4.getColumnFormatter().setWidth( 2, "200px");
//		table4.getColumnFormatter().setWidth( 3, "200px");
		
		FlexTable table4 = addTable(AON.MSG.damageAmount1(), 3);
		
		row = 0;
		addHeaderCell(table4,row, 0,"");
		addHeaderCell(table4,row, 1,"");
		addHeaderCell(table4,row, 2,"Abono");
		addHeaderCell(table4,row, 3,"Compensaci\u00F3n");
		++row;
		paintDescription(table4, "Importe del cr\u00E9dito exigible" , row, 0, false);
		for (int i = 0; i < Mod2002020Constants.DOTACION_KEYS_5.length; i++) {
			Mod2002020Key key = Mod2002020Constants.DOTACION_KEYS_5[i]; 
			if (key != null && callback.getMod200Object().isVisible(key)) {
				paintKeyField(table4,key,row,i+1,10);
			}
		}

		// Exceso cuota líquida positiva ...
//		table5.setWidth("100%");
//		table5.addStyleName(AON.AON_CSS.aonMarginBottom());
//		table5.setCellSpacing(0);
//		table5.getColumnFormatter().setWidth( 0, "auto");
//		table5.getColumnFormatter().setWidth( 1, "200px");
//		table5.getColumnFormatter().setWidth( 2, "200px");
//		table5.getColumnFormatter().setWidth( 3, "200px");
//		table5.getColumnFormatter().setWidth( 4, "200px");
//		row = 0;
//		addHeaderCell(table5,row, 0,"");
//		addHeaderCell(table5,row, 1,AON.MSG.dot50());
//		addHeaderCell(table5,row, 2,AON.MSG.dot51());
//		addHeaderCell(table5,row, 3,AON.MSG.dot52());
//		addHeaderCell(table5,row, 4,AON.MSG.dot53());
//		++row;
		paintKeysProvider(Mod2002020LM1579Key.values(), addTable(AON.MSG.damageAmount6(), 4), new String[] {
				"",
				AON.MSG.dot50(),
				AON.MSG.dot51(),
				AON.MSG.dot52(),
				AON.MSG.dot53()
			});
				
	}

//	private void addHeaderCell(FlexTable table, int row, int col, String msg) {
//		table.setWidget(row, col, new Label( msg ));
//		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
//		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
//		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
//		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontSmall());
//	}
	
	@Override
	protected void populate() {}
}
