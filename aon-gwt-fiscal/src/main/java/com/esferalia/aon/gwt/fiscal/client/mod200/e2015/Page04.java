package com.esferalia.aon.gwt.fiscal.client.mod200.e2015;

import static com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Behaviour.BEHAVIOUR_KEYS_MAP;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015.BalanceType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Widget;

public class Page04 extends PageAbs {

	interface Page4Binder extends
			UiBinder<Widget, Page04> {
	}

	private static final Page4Binder page4Binder = GWT.create(Page4Binder.class);
	
	public Page04() {
		super();
		Widget ui = page4Binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(1, "200px");

		int row = 0;
		for (Mod2002015Key key : Mod2002015Constants.BALANCE_PASIVE_KEYS) {
			if (mod200Object.isVisible(key)) {
				row = paintKey(key,row);
			}
		}
	}
	
	protected boolean isDisabled(Mod2002015Key key) {
		if (mod200Object.getMod200().getBalanceType() == BalanceType.NORMAL) {
			if (key == Mod2002015Key.BP191 
 			 || key == Mod2002015Key.BP195
			 || key == Mod2002015Key.BP202 
			 || key == Mod2002015Key.BP211
			 || key == Mod2002015Key.BP230
			 || key == Mod2002015Key.BP240
			 ) {
				return true;
			}
		}
		Boolean[] behaviour = BEHAVIOUR_KEYS_MAP.get(key.toString());
		return behaviour != null && behaviour[1];
	}
}
