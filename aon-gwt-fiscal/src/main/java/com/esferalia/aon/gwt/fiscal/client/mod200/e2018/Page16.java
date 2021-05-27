package com.esferalia.aon.gwt.fiscal.client.mod200.e2018;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2018.Model2002018.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2018.Mod2002018Key;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class Page16 extends PageAbs {

	private FlexTable table;

	public Page16( Model200PageCallback callback ) {
		super(callback);

		ScrollPanel container = new ScrollPanel();
		container.setStyleName(AON.AON_CSS.aonScrollArea());
		FlowPanel baseContainerPanel = new FlowPanel();
		baseContainerPanel.setStyleName(AON.AON_CSS.aonFiscalContainer());
		
			FlowPanel groupPanel = new FlowPanel();
			groupPanel.setStyleName(AON.AON_CSS.aonGroup());
			
				FlowPanel groupHeaderPanel = new FlowPanel();
				groupHeaderPanel.setStyleName(AON.AON_CSS.aonGroupTitle());
				groupHeaderPanel.add (new InlineLabel(AON.MSG.canariasRegime())); 
				groupPanel.add(groupHeaderPanel);
				
				FlowPanel groupBodyPanel = new FlowPanel();
				groupBodyPanel.setStyleName(AON.AON_CSS.aonGroupBody());
				table = new FlexTable();
				groupBodyPanel.add(table);
				groupPanel.add(groupBodyPanel);
				
			baseContainerPanel.add(groupPanel);
		container.add(baseContainerPanel);
		initWidget(container);
		initializeTable();
	}

	@Override
	protected void initializeTable() {
		table.setWidth("100%");
		table.setCellSpacing(0);
		table.getColumnFormatter().setWidth(0, "auto");
		table.getColumnFormatter().setWidth(1, "200px");
		table.getColumnFormatter().setWidth(2, "200px");
		table.getColumnFormatter().setWidth(3, "200px");
		table.getColumnFormatter().setWidth(4, "200px");
		int row = 0;
		addHeaderCell(table,row, 0,"");
		addHeaderCell(table,row, 1,AON.MSG.canCol1());
		addHeaderCell(table,row, 2,AON.MSG.canCol2());
		addHeaderCell(table,row, 3,AON.MSG.canCol3());
		addHeaderCell(table,row, 4,AON.MSG.canCol4());
		
		++row;
		for (int i = 0; i < Mod2002018Constants.CANARIAS_KEYS.length; i++) {
			Mod2002018Key[] keys = Mod2002018Constants.CANARIAS_KEYS[i];
			paintDescription(table, Mod2002018Constants.CANARIAS_KEYS_DESCRIPTIONS[i], row, 0, false);
			for (int x = 0; x < keys.length; x++) {
				Mod2002018Key key = keys[x]; 
				if (key != null && callback.getMod200Object().isVisible(key)) {
					paintKeyField(table,key,row,x+1,8);					
				}
			}
			++row;
		}
	}
	
	private void addHeaderCell(FlexTable table, int row, int col, String msg) {
		table.setWidget(row, col, new Label( msg ));
		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBold());
		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonBorderBottom());
		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonTextCenter());
		table.getFlexCellFormatter().addStyleName(row, col, AON.AON_CSS.aonFontSmall());
	}

	@Override
	protected void populate() {
	}
	
	@Override
	protected boolean isAvailable() {
		boolean av = super.isAvailable()
  		  && (callback.getMod200Object().getMod200().isChecked(Mod2002018Key.C0029));
		return av;
	}
	
	@Override
	protected boolean isDisabled(Mod2002018Key key) {
		if (key == Mod2002018Key.RC020 || key == Mod2002018Key.RC021)  {
			return callback.getMod200Object().getMod200().isNotChecked(Mod2002018Key.C0045);
		}
		return super.isDisabled(key);
	}
	
}
