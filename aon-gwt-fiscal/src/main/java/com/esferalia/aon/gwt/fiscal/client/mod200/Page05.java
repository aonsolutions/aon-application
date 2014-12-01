package com.esferalia.aon.gwt.fiscal.client.mod200;

import static com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Behaviour.BEHAVIOUR_KEYS_MAP;

import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200.BalanceType;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Constants;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Widget;

public class Page05 extends PageAbs {

	interface Page5Binder extends
			UiBinder<Widget, Page05> {
	}

	private static final Page5Binder page5Binder = GWT.create(Page5Binder.class);
	
	public Page05() {
		Widget ui = page5Binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(1, "250px");

		int row = 0;
		for (Mod200Key key : Mod200Constants.PYG_KEYS) {
			if (mod200Object.isVisible(key)) {
				row = paintKey(key,row);
			}
		}
	}
	protected boolean isDisabled(Mod200Key key) {
		if (mod200Object.getMod200().getBalanceType() == BalanceType.NORMAL) {
			if (key == Mod200Key.PG255
			  ||key == Mod200Key.PG260
			  ||key == Mod200Key.PG279
			  ||key == Mod200Key.PG309
			 ) {
				return true;
			}
		}
		Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(key.toString());
		return behaviour != null && behaviour[1];
	}
}
