package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Model2002016.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class Page12 extends PageAbs {

	interface PageBinder extends
			UiBinder<Widget, Page12> {
	}

	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);

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
		
		int row = 0;
		for (final Mod2002016Key key : Mod2002016Constants.LIQUIDATION_V_KEYS_1) {
			if (callback.getMod200Object().isVisible(key)) {
				row = paintKey(table,key,row);
			}
		}
		
		paintTable(table1, Mod2002016Constants.LIQUIDATION_V_KEYS_2, 2);
		paintTable(table2, Mod2002016Constants.LIQUIDATION_V_KEYS_3, 2);
		paintTable(table3, Mod2002016Constants.LIQUIDATION_V_KEYS_4, 2);
		paintTable(table4, Mod2002016Constants.LIQUIDATION_V_KEYS_5, 3);
	}
	
	private void paintTable(FlexTable table, Mod2002016Key[][] liquidationKeys, int numCols) {
		table.setWidth("100%");
		table.setCellSpacing(0);
		for (int i = 0; i < numCols; i++  ) {
			table.getColumnFormatter().setWidth((i+1), "200px");	
		}
		int row = 0;
		for (Mod2002016Key[] keys : liquidationKeys) {
			boolean paintDescription = true;	
			for (int i = 0; i< keys.length; i++) {
				if (keys[i] != null && callback.getMod200Object().isVisible(keys[i])) {
					if (paintDescription) {
						paintKeyDescription(table, keys[i], row, 0);
						paintDescription = false;
					}
					paintKeyField(table,keys[i],row, i+1);
				}
			}
			row++;
		}
		
		
	}

	@Override
	protected void populate() {}
	
}



