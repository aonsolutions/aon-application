package com.esferalia.aon.gwt.fiscal.client.tree.content.mod200_2014;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014CorrectionKey;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014Key;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Page08 extends PageAbs {
	
	interface Page8Binder extends
			UiBinder<Widget, Page08> {
	}

	private static final Page8Binder page8Binder = GWT
			.create(Page8Binder.class);
	
	public Page08() {
		super();
		Widget ui = page8Binder.createAndBindUi(this);
		initWidget(ui);
	}

	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		
		int row = 0;
		
		Label desc = new Label(AON.MSG.ecpnMsg20());
		desc.setStyleName(AON.AON_CSS.aonBold());
		table.setWidget(row, 0, desc);
		table.getFlexCellFormatter().setColSpan(row, 0, 3);
		table.getFlexCellFormatter().setStyleName(row, 0, AON.AON_CSS.aonTextCenter());
		++row;
		
		paintKeyDescription(table, Mod2002014Key.LQ500, row, 0);
		paintEmptyCell(table, row, 1);
		paintKeyField(table, Mod2002014Key.LQ500, row, 2);
		++row;

		paintKeyDescription(table, Mod2002014Key.LQ301, row, 0);
		paintKeyField(table, Mod2002014Key.LQ301, row, 1);
		table.getFlexCellFormatter().setStyleName(row, 1, AON.AON_CSS.aonWidth150());
		paintKeyField(table, Mod2002014Key.LQ302, row, 2);
		table.getFlexCellFormatter().setStyleName(row, 2, AON.AON_CSS.aonWidth150());
		++row;

		paintKeyDescription(table, Mod2002014Key.LQ501, row, 0);
		paintEmptyCell(table, row, 1);
		paintKeyField(table, Mod2002014Key.LQ501, row, 2);
		++row;
		
		paintEmptyCell(table, row, 0);
		paintEmptyCell(table, row, 1);
		paintEmptyCell(table, row, 2);
		++row;
		
		desc = new Label(AON.MSG.corrections());
		desc.setStyleName(AON.AON_CSS.aonBold());
		table.setWidget(row, 0, desc);
		table.getFlexCellFormatter().setColSpan(row, 0, 0);
		table.setWidget(row, 1, new Label(AON.MSG.increase()));
		table.setWidget(row, 2, new Label(AON.MSG.decrease()));
		++row;
		
		
		
		for (Mod2002014CorrectionKey ck : Mod2002014CorrectionKey.values()) {
			paintKeyDescription(table, ck.isIncreaseEnabled()?ck.getIncrease():ck.getDecrease(), row, 0);
			if (ck.isIncreaseEnabled()) {
				paintKeyField(table, ck.getIncrease(), row, 1);		
			} else {
				paintEmptyCell(table, row, 1);		
			}
			if (ck.isDecreaseEnabled()) {
				paintKeyField(table, ck.getDecrease(), row, 2);		
			} else {
				paintEmptyCell(table, row, 2);		
			}
			++row;
		}
		
		paintKeyDescription(table, Mod2002014Key.I0417, row, 0);
		paintKeyField(table, Mod2002014Key.I0417, row, 1);
		paintKeyField(table, Mod2002014Key.D0418, row, 2);
		++row;
	}
	
}
