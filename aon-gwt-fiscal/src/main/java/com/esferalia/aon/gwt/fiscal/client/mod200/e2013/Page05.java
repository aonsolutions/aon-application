package com.esferalia.aon.gwt.fiscal.client.mod200.e2013;

import static com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Behaviour.BEHAVIOUR_KEYS_MAP;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013.BalanceType;
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
		for (Mod2002013Key key : Mod2002013Constants.PYG_KEYS) {
			if (mod200Object.isVisible(key)) {
				row = paintKey(key,row);
			}
		}
	}
	protected boolean isDisabled(Mod2002013Key key) {
		if (mod200Object.getMod200().getBalanceType() == BalanceType.NORMAL) {
			if (key == Mod2002013Key.PG255
			  ||key == Mod2002013Key.PG260
			  ||key == Mod2002013Key.PG279
			  ||key == Mod2002013Key.PG309
			 ) {
				return true;
			}
		}
		Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(key.toString());
		return behaviour != null && behaviour[1];
	}
}
