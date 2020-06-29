// LIMITACION EN LA DEDUCIBILIDAD DE GASTOS FINANCIEROS
package com.esferalia.aon.gwt.fiscal.client.mod200.e2019;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2019.Model2002019.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2019.Mod2002019Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Page15 extends PageAbs {

	interface PageBinder extends UiBinder<Widget, Page15> {}

	private static final PageBinder pageBinder = GWT.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table;
	@UiField(provided = true)
	FlexTable table1;
	@UiField(provided = true)
	FlexTable table2;
	
	public Page15( Model200PageCallback callback ) {
		super(callback);
		table  = new FlexTable();
		table1 = new FlexTable();
		table2 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		initializeTable();
	}

	@Override
	protected void initializeTable() {
		// -----------------------------------------------------
		//  Limitación en la deducibilidad de gastos financieros.
		// -----------------------------------------------------
		
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "170px");
		table.getColumnFormatter().setWidth(2, "170px");
		int row = 0;

		for (int i = 0; i < Mod2002019Constants.DEDUCIBLE_LIMITATION_KEYS_1.length; i++) {
			Mod2002019Key[] keys = Mod2002019Constants.DEDUCIBLE_LIMITATION_KEYS_1[i];
			if (keys == null) {
				if (i == 0) {
					paintDescription(table, AON.MSG.limitMsg1() , row, 0, true);
				} else {
					paintDescription(table, AON.MSG.limitMsg2() , row, 0, true);
				}
			} else {
				for (int x = 0; x < keys.length; x++) {
					Mod2002019Key key = keys[x]; 
					if (key != null && callback.getMod200Object().isVisible(key)) {
						paintDescription(table, key.getDescription(), row, 0, false, 95);
						if (key == Mod2002019Key.LM1250 || key == Mod2002019Key.LM1251
						 || key == Mod2002019Key.LM1252 || key == Mod2002019Key.LM1253
						 || key == Mod2002019Key.LM1254) {
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
		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(0, "auto");
		table1.getColumnFormatter().setWidth(1, "180px");
		table1.getColumnFormatter().setWidth(2, "180px");
		table1.getColumnFormatter().setWidth(3, "180px");
		table1.getColumnFormatter().setWidth(4, "180px");
		table1.getColumnFormatter().setWidth(5, "180px");
		row = 0;
		addHeaderCell(table1,row, 1,AON.MSG.liquiMsg2());
		table1.getFlexCellFormatter().setColSpan(row, 1, 2);
		addHeaderCell(table1,row, 3,AON.MSG.liquiMsg4());
		table1.getFlexCellFormatter().setColSpan(row, 3, 2);
		++row;
		addHeaderCell(table1,row, 0,AON.MSG.fiscalYear());
		addHeaderCell(table1,row, 1,AON.MSG.liquiMsg21());
		addHeaderCell(table1,row, 2,AON.MSG.remainder());
		addHeaderCell(table1,row, 3,AON.MSG.liquiMsg3());
		addHeaderCell(table1,row, 4,AON.MSG.liquiMsg21());
		addHeaderCell(table1,row, 5,AON.MSG.remainder());
		++row;
		for (int i = 0; i < Mod2002019Constants.DEDUCIBLE_LIMITATION_KEYS_2.length; i++) {
			Mod2002019Key[] keys = Mod2002019Constants.DEDUCIBLE_LIMITATION_KEYS_2[i];
			paintDescription(table1, Mod2002019Constants.DEDUCIBLE_LIMITATION_KEYS_DESCRIPTIONS_2[i], row, 0, false);
			for (int x = 0; x < keys.length; x++) {
				Mod2002019Key key = keys[x]; 
				if (key != null && callback.getMod200Object().isVisible(key)) {
					paintKeyField(table1,key,row,x+1,10);
				}
			}
			++row;
		}
		
		// ---------------------------------------------------------------		 
		// Pendiente de adición por límite beneficio operativo no aplicado
		// ---------------------------------------------------------------
		table2.setWidth("100%");
		table2.setCellSpacing(0);
		table2.getColumnFormatter().setWidth(1, "200px");
		table2.getColumnFormatter().setWidth(2, "200px");
		table2.getColumnFormatter().setWidth(3, "200px");
		row = 0;
		addHeaderCell(table2,row, 0,AON.MSG.liquiMsg1());
		addHeaderCell(table2,row, 1,AON.MSG.liquiMsg2());
		addHeaderCell(table2,row, 2,AON.MSG.liquiMsg3());
		addHeaderCell(table2,row, 3,AON.MSG.liquiMsg4());
		++row;
		for (int i = 0; i < Mod2002019Constants.DEDUCIBLE_LIMITATION_KEYS_3.length; i++) {
			Mod2002019Key[] keys = Mod2002019Constants.DEDUCIBLE_LIMITATION_KEYS_3[i];
			paintDescription(table2, Mod2002019Constants.DEDUCIBLE_LIMITATION_KEYS_DESCRIPTIONS_3[i], row, 0, false);
			for (int x = 0; x < keys.length; x++) {
				Mod2002019Key key = keys[x]; 
				if (key != null && callback.getMod200Object().isVisible(key)) {
					paintKeyField(table2,key,row,x+1,10);
				}
			}
			++row;
		}
	}
	
	private void addHeaderCell(FlexTable table, int row, int col, String msg) {
		table.setWidget(row, col, new Label( msg ));
		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontSmall());
	}

	@Override
	protected void populate() {}
	
	
	@Override
	protected boolean isAvailable() {
		boolean av = super.isAvailable()
  		  && callback.getMod200Object().getMod200().isNotChecked(Mod2002019Key.C0009) 
  		  &&  callback.getMod200Object().getMod200().isNotChecked(Mod2002019Key.C0010);
		return av;
	}
	
}
