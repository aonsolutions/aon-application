package com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2015;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.tree.node.Mod2002015TreeObject;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2015.Mod2002015Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Page12 extends PageAbs {

	interface PageBinder extends
			UiBinder<Widget, Page12> {
	}

	private static final PageBinder pageBinder = GWT
			.create(PageBinder.class);

	@UiField(provided = true)
	FlexTable table1;
	@UiField(provided = true)
	FlexTable table2;
	
	TextBox nrsAnexoIII;
	TextBox justCanarias;
	TextBox nrsAnexoIV;
	TextBox nrsAnexoV;

	public Page12() {
		super();
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

		table2.setWidth("100%");
		table2.setCellSpacing(0);
		table2.getColumnFormatter().setWidth(1, "300px");
		paintDescription(table2, AON.MSG.nrsAnexoIII(), 0, 0, false);
		table2.setWidget(0, 1, nrsAnexoIII);
		paintDescription(table2, AON.MSG.justCanarias(), 1, 0, false);
		table2.setWidget(1, 1, justCanarias);
		paintDescription(table2, AON.MSG.nrsAnexoIV(), 2, 0, false);
		table2.setWidget(2, 1, nrsAnexoIV);
		paintDescription(table2, AON.MSG.nrsAnexoV(), 3, 0, false);
		table2.setWidget(3, 1, nrsAnexoV);
		
	}

	@Override
	public void dump(Mod2002015TreeObject mod200) {
		super.dump(mod200);
		nrsAnexoIII.setValue( mod200Object.getMod200().getNrsAnexoIII());
		justCanarias.setValue( mod200Object.getMod200().getJustCanarias());
		nrsAnexoIV.setValue( mod200Object.getMod200().getNrsAnexoIV());
		nrsAnexoV.setValue( mod200Object.getMod200().getNrsAnexoV());
	}
	
	public void populate(Mod2002015TreeObject mod200Object) {
		mod200Object.getMod200().setNrsAnexoIII(nrsAnexoIII.getValue());
		mod200Object.getMod200().setJustCanarias(justCanarias.getValue());
		mod200Object.getMod200().setNrsAnexoIV(nrsAnexoIV.getValue());
		mod200Object.getMod200().setNrsAnexoV(nrsAnexoV.getValue());
	}

}
