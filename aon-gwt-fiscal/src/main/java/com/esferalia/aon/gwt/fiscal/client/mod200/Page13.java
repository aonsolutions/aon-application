package com.esferalia.aon.gwt.fiscal.client.mod200;

import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Constants;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Widget;

public class Page13 extends PageAbs {

	interface PageBinder extends
			UiBinder<Widget, Page13> {
	}

	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);
	
	public Page13() {
		super();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}

	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(0, "auto");
		cf.setWidth(1, "250px");
		int row = 0;
		for (final Mod200Key key : Mod200Constants.DEDUCIBLE_LIMITATION_KEYS) {
			row = paintKey(table,key,row);
		}
	}

}
