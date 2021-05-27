package com.esferalia.aon.gwt.fiscal.client.mod200.e2015;

import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Behaviour.BEHAVIOUR_KEYS_MAP;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015.BalanceType;
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
		cf.setWidth(1, "200px");

		int row = 0;
		for (Mod2002015Key key : Mod2002015Constants.ECPN_INCOME_KEYS) {
			if (mod200Object.isVisible(key) ) {
				row = paintKey(key,row);
			}
		}
	}
	
	protected boolean isDisabled(Mod2002015Key key) {
		if (mod200Object.getMod200().getBalanceType() == BalanceType.NORMAL) {
			if (key == Mod2002015Key.T0336
			  ||key == Mod2002015Key.T0346
			 ) {
				return true;
			}
		}
		Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(key.toString());
		return behaviour != null && behaviour[1];
	}
}
