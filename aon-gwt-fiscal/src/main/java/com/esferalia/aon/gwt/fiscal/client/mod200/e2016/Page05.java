package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Model2002016.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Widget;

public class Page05 extends PageAbs {

	interface Page5Binder extends
			UiBinder<Widget, Page05> {
	}

	private static final Page5Binder page5Binder = GWT.create(Page5Binder.class);
	
	public Page05( Model200PageCallback callback ) {
		super(callback);
		Widget ui = page5Binder.createAndBindUi(this);
		initWidget(ui);
	}
	
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(1, "200px");

		int row = 0;
		for (Mod2002016Key key : Mod2002016Constants.PYG_KEYS) {
			if (callback.getMod200Object().isVisible(key)) {
				row = paintKey(key,row);
			}
		}
	}
	
	@Override
	protected void populate() {}
	
	@Override
	protected boolean isDisabled(Mod2002016Key key) {
		if (callback.getMod200Object().getMod200().getPygType() == BalanceType.NORMAL) {
			if (key == Mod2002016Key.PG255
			  ||key == Mod2002016Key.PG279
			  ||key == Mod2002016Key.PG309
			 ) {
				return true;
			}
		}
		return super.isDisabled(key);
	}
}
