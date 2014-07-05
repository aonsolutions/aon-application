package com.esferalia.aon.gwt.fiscal.client.mod200;

import static com.esferalia.aon.gwt.fiscal.client.mod200.Model200.MSG;

import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BNLQ547Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200BNLQ579Key;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Constants;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.Widget;

public class Page09 extends PageAbs {
	
	interface PageBinder extends
			UiBinder<Widget, Page09> {
	}

	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);
	private static final String[] HEADERS_2 = new String[]{"",
		 MSG.previousPending()
		,MSG.current()
		,MSG.futurePending()		
	};

	public Page09() {
		super();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	protected void initializeTable() {
		
		table.setWidth("100%");
		table.setCellSpacing(0);
		
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(0, "auto");
		cf.setWidth(1, "250px");

		int row = 0;
		for (Mod200Key key : Mod200Constants.LIQUIDATION_II_KEYS) {
			if (mod200Object.isVisible(key)) {
				row = paintKey(key,row);
				if (key == Mod200Key.LQ579) {
					row = paintKeyBreakdown(table,row,Mod200Key.LQ579.getDescription()
							,Mod200BNLQ579Key.values(),null);
				}
				if (key == Mod200Key.LQ547) {
					row = paintKeyBreakdown(table,row,Mod200Key.LQ547.getDescription()
							,Mod200BNLQ547Key.values(),HEADERS_2);
				}
			}
		}
	}
}
