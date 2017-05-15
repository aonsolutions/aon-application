package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Model2002016.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Widget;

public class Page04 extends PageAbs {

	interface Page4Binder extends
			UiBinder<Widget, Page04> {
	}

	private static final Page4Binder page4Binder = GWT.create(Page4Binder.class);
	
	public Page04( Model200PageCallback callback ) {
		super(callback);
		Widget ui = page4Binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(1, "200px");

		int row = 0;
		for (Mod2002016Key key : Mod2002016Constants.BALANCE_PASIVE_KEYS) {
			if (callback.getMod200Object().isVisible(key)) {
				row = paintKey(key,row);
			}
		}
	}

	@Override
	protected void populate() {}
	@Override	
	protected boolean isDisabled(Mod2002016Key key) {
		if (callback.getMod200Object().getMod200().getBalanceType() == BalanceType.NORMAL) {
			if (key == Mod2002016Key.BP191 
 			 || key == Mod2002016Key.BP195
			 || key == Mod2002016Key.BP202 
			 || key == Mod2002016Key.BP211
			 || key == Mod2002016Key.BP230
			 || key == Mod2002016Key.BP240
			 ) {
				return true;
			}
		}
		return super.isDisabled(key);
	}
}
