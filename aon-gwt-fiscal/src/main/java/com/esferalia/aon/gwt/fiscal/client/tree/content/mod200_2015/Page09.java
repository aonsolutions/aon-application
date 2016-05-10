package com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015LQ547Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015LQ554Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015LQ561Key;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015LQ579Key;
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
		 AON.MSG.previousPending()
		,AON.MSG.current()
		,AON.MSG.futurePending()		
	};
	private static final String[] HEADERS_1 = new String[]{""
		,AON.MSG.cooperativeResult()
	 	,AON.MSG.extraCooperativeResult()
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
		cf.setWidth(1, "200px");

		int row = 0;
		for (Mod2002015Key key : Mod2002015Constants.LIQUIDATION_II_KEYS) {
			if (mod200Object.isVisible(key)) {
				row = paintKey(key,row);
				if (key == Mod2002015Key.LQ554) {
					row = paintKeyBreakdownLink(table,row,AON.MSG.cooperativeRegime()
							,Mod2002015LQ554Key.values(),HEADERS_1);
				}
				if (key == Mod2002015Key.LQ561) {
					row = paintKeyBreakdownLink(table,row,Mod2002015Key.LQ561.getDescription()
							,Mod2002015LQ561Key.values(),HEADERS_2);
				}
				if (key == Mod2002015Key.LQ579) {
					row = paintKeyBreakdownLink(table,row,Mod2002015Key.LQ579.getDescription()
							,Mod2002015LQ579Key.values(),null);
				}
				if (key == Mod2002015Key.LQ547) {
					row = paintKeyBreakdownLink(table,row,Mod2002015Key.LQ547.getDescription()
							,Mod2002015LQ547Key.values(),HEADERS_2);
				}
			}
		}
	}
}
