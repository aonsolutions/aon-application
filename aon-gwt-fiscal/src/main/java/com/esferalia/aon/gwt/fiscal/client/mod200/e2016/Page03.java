package com.esferalia.aon.gwt.fiscal.client.mod200.e2016;

import com.esferalia.aon.gwt.fiscal.client.mod200.e2016.Model2002016.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2016.Mod2002016Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Widget;

public class Page03 extends PageAbs {

	interface Page3Binder extends
			UiBinder<Widget, Page03> {
	}

	private static final Page3Binder page3Binder = GWT.create(Page3Binder.class);
	
	public Page03( Model200PageCallback callback ) {
		super(callback);
		Widget ui = page3Binder.createAndBindUi(this);
		initWidget(ui);
		initializeTable();
	}
	
	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(1, "200px");

		int row = 0;
		for (Mod2002016Key key : Mod2002016Constants.BALANCE_ACTIVE_KEYS) {
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
			if (key == Mod2002016Key.BA111
			 || key == Mod2002016Key.BA115
			 || key == Mod2002016Key.BA138 
			 || key == Mod2002016Key.BA177 ) {
				return true;
			}
		}
		return super.isDisabled(key);
	}
}
