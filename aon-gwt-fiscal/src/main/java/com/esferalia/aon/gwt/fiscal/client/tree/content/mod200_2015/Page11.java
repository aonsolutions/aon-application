package com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class Page11 extends PageAbs {

	interface PageBinder extends
			UiBinder<Widget, Page11> {
	}

	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table1;
	@UiField(provided = true)
	FlexTable table2;
	@UiField(provided = true)
	FlexTable table3;
	
	public Page11() {
		super();
		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}
	
	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		
		int row = 0;
		for (final Mod2002015Key key : Mod2002015Constants.LIQUIDATION_IV_KEYS_1) {
			if (mod200Object.isVisible(key)) {
				row = paintKey(table,key,row);
			}
		}
		
		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(1, "200px");
		table1.getColumnFormatter().setWidth(2, "200px");
		for (int i = 0; i < Mod2002015Constants.LIQUIDATION_IV_KEYS_2.length; i++) {
			Mod2002015Key key = Mod2002015Constants.LIQUIDATION_IV_KEYS_2[i];
			if (mod200Object.isVisible(key)) {
				if (i%2 == 0) {
					paintKey(table1,key, (i/2));
				} else {
					paintKeyField(table1,key,(i/2),2);
				}
			}
		}

		table2.setWidth("100%");
		table2.setCellSpacing(0);
		table2.getColumnFormatter().setWidth(1, "200px");
		table2.getColumnFormatter().setWidth(2, "200px");
		for (int i = 0; i < Mod2002015Constants.LIQUIDATION_IV_KEYS_3.length; i++) {
			Mod2002015Key key = Mod2002015Constants.LIQUIDATION_IV_KEYS_3[i];
			if (i%2 == 0) {
				if (mod200Object.isVisible(key)) {
					paintKey(table2,key,(i/2));
				}
			} else {
				if (key != null && mod200Object.isVisible(key)) {
					paintKeyField(table2,key,(i/2),2);
				}
			}
		}
		
	}

}
