package com.esferalia.aon.gwt.fiscal.client.mod200;

import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Constants;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Widget;

public class Page11 extends PageAbs {

	interface PageBinder extends
			UiBinder<Widget, Page11> {
	}

	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table1;
	@UiField(provided = true)
	FlexTable table2;
	
	private FlexTable[] tables;
	private Mod200Key[][] keys;

	public Page11() {
		super();
		table1 = new FlexTable();
		table2 = new FlexTable();

		tables = new FlexTable[]{table,table1,table2};
		keys = new Mod200Key[][]{Mod200Constants.LIQUIDATION_IV_KEYS_1
				,Mod200Constants.LIQUIDATION_IV_KEYS_2,Mod200Constants.LIQUIDATION_IV_KEYS_3};
		
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	@Override
	protected void initializeTable() {
		for (int i = 0; i < tables.length; i++) {
			FlexTable tab = tables[i];
			
			tab.setWidth("100%");
			tab.setCellSpacing(0);
			
			ColumnFormatter cf = tab.getColumnFormatter();
			cf.setWidth(0, "auto");
			cf.setWidth(1, "250px");
			int row = 0;
			for (final Mod200Key key : keys[i]) {
				row = paintKey(tab,key,row);
			}
		}
		
	}

}
