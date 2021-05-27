package com.esferalia.aon.gwt.fiscal.client.mod200.e2014;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
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
	@UiField(provided = true)
	FlexTable table4;
	
	public Page13() {
		super();
		table1 = new FlexTable();
		table2 = new FlexTable();
		table3 = new FlexTable();
		table4 = new FlexTable();
		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
	}

	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		int row = 0;
		for (final Mod2002014Key key : Mod2002014Constants.DEDUCIBLE_LIMITATION_KEYS_1) {
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
		table1.setWidget(row, 0, new Label(AON.MSG.liquiMsg1() ));
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 1, new Label(AON.MSG.liquiMsg2() ));
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 2, new Label(AON.MSG.liquiMsg3() ));
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table1.setWidget(row, 3, new Label(AON.MSG.liquiMsg4() ));
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table1.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		++row;
		for (int i = 0; i < Mod2002014Constants.DEDUCIBLE_LIMITATION_KEYS_2.length; i++) {
			Mod2002014Key key = Mod2002014Constants.DEDUCIBLE_LIMITATION_KEYS_2[i];
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
		table2.setWidget(row, 0, new Label(AON.MSG.liquiMsg1() ));
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table2.setWidget(row, 1, new Label(AON.MSG.liquiMsg2() ));
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table2.setWidget(row, 2, new Label(AON.MSG.liquiMsg3() ));
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table2.setWidget(row, 3, new Label(AON.MSG.liquiMsg4() ));
		table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table2.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		++row;
		for (int i = 0; i < Mod2002014Constants.DEDUCIBLE_LIMITATION_KEYS_3.length; i++) {
			Mod2002014Key key = Mod2002014Constants.DEDUCIBLE_LIMITATION_KEYS_3[i];
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
		table3.setWidget(row, 0, new Label(AON.MSG.liquiMsg1() ));
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 1, new Label(AON.MSG.liquiMsg2() ));
		table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 1, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 2, new Label(AON.MSG.liquiMsg3() ));
		table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 2, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 3, new Label(AON.MSG.liquiMsg5() ));
		table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 3, AON.AON_CSS.aonTextCenter());
		table3.setWidget(row, 4, new Label(AON.MSG.liquiMsg4() ));
		table3.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBold());
		table3.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonBorderBottom());
		table3.getFlexCellFormatter().addStyleName(row, 4, AON.AON_CSS.aonTextCenter());
		++row;
		for (int i = 0; i < Mod2002014Constants.DEDUCIBLE_LIMITATION_KEYS_4.length; i++) {
			Mod2002014Key key = Mod2002014Constants.DEDUCIBLE_LIMITATION_KEYS_4[i];
			if (mod200Object.isVisible(key)) {
				if (mod200Object.isVisible(key)) {
					int col = (i+1)%4;
					if (col == 1) {
						row = paintKey(table3,key,row);
					} if (col == 2) { 
						paintKeyField(table3,key,row-1,2);
					} if (col == 3) { 
						paintKeyField(table3,key,row-1,3);
					} if (col == 0) {
						paintKeyField(table3,key,row-1,4);
					} 
				}
			}
		}
		
		table4.setWidth("100%");
		table4.setCellSpacing(0);
		table4.getColumnFormatter().setWidth(1, "250px");
		row = 0;
		for (final Mod2002014Key key : Mod2002014Constants.DEDUCIBLE_LIMITATION_KEYS_5) {
			if (mod200Object.isVisible(key)) {
				row = paintKey(table4,key,row);
			}
		}
	}

}
