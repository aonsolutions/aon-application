package com.esferalia.aon.gwt.fiscal.client.mod200.e2013;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;

public class Page13 extends PageAbs {

	interface PageBinder extends
			UiBinder<Widget, Page13> {
	}

	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table1;
	@UiField(provided = true)
	FlexTable table2;
	@UiField(provided = true)
	FlexTable table3;
	
	public Page13() {
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
		table.getColumnFormatter().setWidth(1, "250px");
		int row = 0;
		for (final Mod2002013Key key : Mod2002013Constants.DEDUCIBLE_LIMITATION_KEYS_1) {
			if (mod200Object.isVisible(key)) {
				row = paintKey(table,key,row);
			}
		}
		
		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(1, "200px");
		table1.getColumnFormatter().setWidth(2, "200px");
		table1.getColumnFormatter().setWidth(3, "200px");
		row = 0;
		for (int i = 0; i < Mod2002013Constants.DEDUCIBLE_LIMITATION_KEYS_2.length; i++) {
			Mod2002013Key key = Mod2002013Constants.DEDUCIBLE_LIMITATION_KEYS_2[i];
			if (mod200Object.isVisible(key)) {
				int col = (i+1)%3;
				if (col == 1) {
					row = paintKey(table1,key,row);
				} if (col == 2) { 
					paintKeyField(table1,key,row-1,2);
				} if (col == 0) {
					paintKeyField(table1,key,row-1,3);
				} 
			}
		}
		
		table2.setWidth("100%");
		table2.setCellSpacing(0);
		table2.getColumnFormatter().setWidth(1, "200px");
		table2.getColumnFormatter().setWidth(2, "200px");
		table2.getColumnFormatter().setWidth(3, "200px");
		row = 0;
		for (int i = 0; i < Mod2002013Constants.DEDUCIBLE_LIMITATION_KEYS_3.length; i++) {
			Mod2002013Key key = Mod2002013Constants.DEDUCIBLE_LIMITATION_KEYS_3[i];
			if (mod200Object.isVisible(key)) {
				int col = (i+1)%3;
				if (col == 1) {
					row = paintKey(table2,key,row);
				} if (col == 2) { 
					paintKeyField(table2,key,row-1,2);
				} if (col == 0) {
					paintKeyField(table2,key,row-1,3);
				} 
			}
		}
		
		table3.setWidth("100%");
		table3.setCellSpacing(0);
		table3.getColumnFormatter().setWidth(1, "250px");
		row = 0;
		for (final Mod2002013Key key : Mod2002013Constants.DEDUCIBLE_LIMITATION_KEYS_4) {
			if (mod200Object.isVisible(key)) {
				row = paintKey(table3,key,row);
			}
		}
		
	}

}
