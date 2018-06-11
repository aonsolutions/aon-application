package com.esferalia.aon.gwt.fiscal.client.mod200.e2017;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2017.Model2002017.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Page14 extends PageAbs {

	interface PageBinder extends
			UiBinder<Widget, Page14> {
	}

	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table;
	@UiField(provided = true)
	FlexTable table1;
	@UiField(provided = true)
	FlexTable table2;
	
	TextBox nrsAnexoIII;
	TextBox justCanarias;
	TextBox nrsAnexoIV;
	TextBox nrsAnexoV;
	TextBox justActivos;

	public Page14( Model200PageCallback callback ) {
		super(callback);
		table  = new FlexTable();
		table1 = new FlexTable();
		table2 = new FlexTable();
		
		nrsAnexoIII = new TextBox();
		nrsAnexoIII.setVisibleLength(20);
		nrsAnexoIII.setStyleName(AON.AON_CSS.aonInputText());
		
		justCanarias = new TextBox();
		justCanarias.setVisibleLength(20);
		justCanarias.setStyleName(AON.AON_CSS.aonInputText());
		
		nrsAnexoIV = new TextBox();
		nrsAnexoIV.setVisibleLength(20);
		nrsAnexoIV.setStyleName(AON.AON_CSS.aonInputText());
		
		nrsAnexoV = new TextBox();
		nrsAnexoV.setVisibleLength(20);
		nrsAnexoV.setStyleName(AON.AON_CSS.aonInputText());
		
		justActivos = new TextBox();
		justActivos.setVisibleLength(20);
		justActivos.setStyleName(AON.AON_CSS.aonInputText());

		Widget ui = pageBinder.createAndBindUi(this);
		initWidget(ui);
		initializeTable();
	}

	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(1, "200px");
		int row = 0;
		for (final Mod2002017Key key : Mod2002017Constants.INCOME_DISTRIBUTION_KEYS_1) {
			if (callback.getMod200Object().isVisible(key)) {
				row = paintKey(table,key,row);
			}
		}

		table1.setWidth("100%");
		table1.setCellSpacing(0);
		table1.getColumnFormatter().setWidth(1, "200px");
		row = 0;
		for (final Mod2002017Key key : Mod2002017Constants.INCOME_DISTRIBUTION_KEYS_2) {
			if (callback.getMod200Object().isVisible(key)) {
				row = paintKey(table1,key,row);
				if (key == Mod2002017Key.ID1270 || key == Mod2002017Key.ID1271 || key == Mod2002017Key.ID1522) {
					table1.getFlexCellFormatter().addStyleName((row-1), 0, AON.AON_CSS.aonPadding2Left());
				}
				
			}
		}

		table2.setWidth("100%");
		table2.setCellSpacing(0);
		table2.getColumnFormatter().setWidth(1, "200px");
		paintDescription(table2, AON.MSG.nrsAnexoIII(), 0, 0, false);
		table2.setWidget(0, 1, nrsAnexoIII);
		
		paintDescription(table2, AON.MSG.nrsAnexoIV(), 1, 0, false);
		table2.setWidget(1, 1, nrsAnexoIV);
		
		paintDescription(table2, AON.MSG.nrsAnexoV(), 2, 0, false);
		table2.setWidget(2, 1, nrsAnexoV);

		paintDescription(table2, AON.MSG.justCanarias(), 3, 0, false);
		table2.setWidget(3, 1, justCanarias);
		
		paintDescription(table2, AON.MSG.justActivos(), 4, 0, false);
		table2.setWidget(4, 1, justActivos);
		
	}

	@Override
	public void dump() {
		super.dump();
		nrsAnexoIII.setValue( callback.getMod200Object().getMod200().getNrsAnexoIII());
		justCanarias.setValue( callback.getMod200Object().getMod200().getJustCanarias());
		nrsAnexoIV.setValue( callback.getMod200Object().getMod200().getNrsAnexoIV());
		nrsAnexoV.setValue( callback.getMod200Object().getMod200().getNrsAnexoV());
		justActivos.setValue( callback.getMod200Object().getMod200().getJustActivos());
	}
	@Override
	public void populate() {
		callback.getMod200Object().getMod200().setNrsAnexoIII(nrsAnexoIII.getValue());
		callback.getMod200Object().getMod200().setJustCanarias(justCanarias.getValue());
		callback.getMod200Object().getMod200().setNrsAnexoIV(nrsAnexoIV.getValue());
		callback.getMod200Object().getMod200().setNrsAnexoV(nrsAnexoV.getValue());
		callback.getMod200Object().getMod200().setJustActivos(justActivos.getValue());
	}

}
