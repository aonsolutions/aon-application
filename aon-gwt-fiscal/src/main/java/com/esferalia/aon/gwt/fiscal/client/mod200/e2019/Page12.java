// LIQUIDACION (V): CUOTA A INGRESAR O DEVOLVER, PAGOS FRACCIONADOS, LIQUIDO A INGRESAR O DEVOLVER
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

public class Page12 extends PageAbs {

	interface PageBinder extends
			UiBinder<Widget, Page12> {
	}

	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table;
	@UiField(provided = true)
	FlexTable table1;
	@UiField(provided = true)
	FlexTable table2;
	@UiField(provided = true)
	FlexTable table3;
	@UiField(provided = true)
	FlexTable table4;
	
	public Page12( Model200PageCallback callback ) {
		super(callback);
		table  = new FlexTable();
		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		table4 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		initializeTable();
	}
	
	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		
		paintTable(table , Mod2002019Constants.LIQUIDATION_V_KEYS_1, 2, "retention");
		paintTable(table1, Mod2002019Constants.LIQUIDATION_V_KEYS_2, 2, "diputation");
		paintTable(table2, Mod2002019Constants.LIQUIDATION_V_KEYS_3, 2, null);
		paintTable(table3, Mod2002019Constants.LIQUIDATION_V_KEYS_4, 2, null);
		paintTable(table4, Mod2002019Constants.LIQUIDATION_V_KEYS_5, 3, null);
	}
	
	private void paintTable(FlexTable table, Mod2002019Key[][] liquidationKeys, int numCols, String header) {
		table.setWidth("100%");
		table.setCellSpacing(0);
		for (int i = 0; i < numCols; i++  ) {
			table.getColumnFormatter().setWidth((i+1), "200px");	
		}
		
		int row = 0;
		
		if (header=="retention") {
			table.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonPaddingLeft());
			table.getFlexCellFormatter().setColSpan(row, 0, 0);
			table.setWidget(row, 1, new Label("Efectuados a la entidad"));
			table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
			table.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
			table.setWidget(row, 2, new Label("Imputados por AIEs y UTEs"));
			table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
			table.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
			++row;
		} 
		
		for (Mod2002019Key[] keys : liquidationKeys) {
			
			boolean paintDescription = true;							
			for (int i = 0; i < keys.length; i++) {
				if (keys[i] != null) {
					if (paintDescription) {
						paintKeyDescription(table, keys[i], row, 0);
						paintDescription = false;
					}
					paintKeyField(table,keys[i], row, i+1);
				}
			}
			row++;
			
		}
		
		
	}

	@Override
	protected void populate() {}
	
}



