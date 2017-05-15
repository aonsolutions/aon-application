package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Model2002016.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Widget;

public class Page06 extends PageAbs {

	interface Page6Binder extends
			UiBinder<Widget, Page06> {
	}

	private static final Page6Binder page6Binder = GWT.create(Page6Binder.class);
	
	public Page06( Model200PageCallback callback ) {
		super(callback);
		Widget ui = page6Binder.createAndBindUi(this);
		initWidget(ui);
	}
	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(1, "200px");

		int row = 0;
		for (Mod2002016Key key : Mod2002016Constants.ECPN_INCOME_KEYS) {
			if (callback.getMod200Object().isVisible(key) ) {
				row = paintKey(key,row);
			}
		}
	}
	
	@Override
	protected void populate() {}
	
	@Override
	protected boolean isDisabled(Mod2002016Key key) {
		if (callback.getMod200Object().getMod200().getBalanceType() == BalanceType.NORMAL) {
			if (key == Mod2002016Key.T0336
			  ||key == Mod2002016Key.T0346
			 ) {
				return true;
			}
		}
		return super.isDisabled(key);
	}
}
