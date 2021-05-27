package com.esferalia.aon.gwt.fiscal.client.mod200.e2017;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.fiscal.client.mod200.e2017.Model2002017.Model200PageCallback;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017.BalanceType;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017Constants;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017Key;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLTable.ColumnFormatter;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class Page04 extends PageAbs {

	private FlexTable table;

	public Page04( Model200PageCallback callback ) {
		super(callback);
		ScrollPanel container = new ScrollPanel();
		container.setStyleName(AON.AON_CSS.aonScrollArea());
		FlowPanel baseContainerPanel = new FlowPanel();
		baseContainerPanel.setStyleName(AON.AON_CSS.aonFiscalContainer());
		
		FlowPanel groupPanel= new FlowPanel();
		groupPanel.setStyleName(AON.AON_CSS.aonGroup());
			
				FlowPanel groupHeaderPanel = new FlowPanel();
				groupHeaderPanel.setStyleName(AON.AON_CSS.aonGroupTitle());
				groupHeaderPanel.add (new InlineLabel(AON.MSG.balancePasivo())); 
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
		
		ColumnFormatter cf = table.getColumnFormatter();
		cf.setWidth(1, "200px");

		int row = 0;
		for (Mod2002017Key key : Mod2002017Constants.BALANCE_PASIVE_KEYS) {
			if (callback.getMod200Object().isVisible(key)) {
				row = paintKey(table,key,row);
			}
		}
	}

	@Override
	protected void populate() {}
	@Override	
	protected boolean isDisabled(Mod2002017Key key) {
		if (callback.getMod200Object().getMod200().getBalanceType() == BalanceType.NORMAL) {
			if (key == Mod2002017Key.BP191 
 			 || key == Mod2002017Key.BP195
			 || key == Mod2002017Key.BP202 
			 || key == Mod2002017Key.BP211
			 || key == Mod2002017Key.BP230
			 || key == Mod2002017Key.BP240
			 ) {
				return true;
			}
		}
		return super.isDisabled(key);
	}
}
