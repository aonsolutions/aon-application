package com.esferalia.aon.gwt.fiscal.client.mod200;

import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Constants;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Widget;

public class Page06 extends PageAbs {

	interface Page6Binder extends
			UiBinder<Widget, Page06> {
	}

	private static final Page6Binder page6Binder = GWT.create(Page6Binder.class);
	
	public Page06() {
		super();
		Widget ui = page6Binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(0, "auto");
		cf.setWidth(1, "250px");

		int row = 0;
		for (Mod200Key key : Mod200Constants.ECPN_INCOME_KEYS) {
			if (mod200Object.isVisible(key) ) {
				row = paintKey(key,row);
			}
		}
	}
	
}
