package com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class Page12 extends PageAbs {

	interface PageBinder extends
			UiBinder<Widget, Page12> {
	}

	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table1;

	public Page12() {
		super();
		table1 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}

	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		int row = 0;
		for (final Mod2002015Key key : Mod2002015Constants.INCOME_DISTRIBUTION_KEYS_1) {
			if (mod200Object.isVisible(key)) {
				row = paintKey(table,key,row);
			}
		}

		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(1, "200px");
		row = 0;
		for (final Mod2002015Key key : Mod2002015Constants.INCOME_DISTRIBUTION_KEYS_2) {
			if (mod200Object.isVisible(key)) {
				row = paintKey(table1,key,row);
			}
		}
}

}
